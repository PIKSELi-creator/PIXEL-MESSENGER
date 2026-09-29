from pathlib import Path
from datetime import datetime
import shutil

BASE = Path(__file__).resolve().parent
SRC = BASE / "src" / "main" / "java"

FILES = {
    "Main.java": """import java.nio.file.Path;

public class Main {

    private static final String VERSION = "10.0";

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("       PIXEL CHAT SERVER");
        System.out.println("          Java " + VERSION);
        System.out.println("================================");

        try {

            UserStore userStore =
                    new UserStore(Path.of("data/users.dat"));

            VerificationService verificationService =
                    new VerificationService();

            AuthService authService =
                    new AuthService(
                            userStore,
                            verificationService
                    );

            SessionStore sessionStore =
                    new SessionStore(
                            Path.of("data/sessions.dat")
                    );

            System.out.println("UserStore: ONLINE");
            System.out.println("AuthService: ONLINE");
            System.out.println("VerificationService: ONLINE");
            System.out.println("SessionStore: ONLINE");

            ApiServer apiServer =
                    new ApiServer(
                            authService,
                            sessionStore
                    );

            System.out.println("API: ONLINE");

            apiServer.start();

        } catch (Exception e) {

            System.out.println(
                    "КРИТИЧЕСКАЯ ОШИБКА SERVER 10.0"
            );

            e.printStackTrace();
        }
    }
}
""",

    "ApiServer.java": """import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiServer {

    private static final int PORT = 5000;
    private static final int MAX_REQUEST_LENGTH = 8192;

    private final AuthService authService;
    private final SessionStore sessionStore;

    private final RateLimiter rateLimiter =
            new RateLimiter();

    private final ExecutorService pool =
            Executors.newCachedThreadPool();

    public ApiServer(
            AuthService authService,
            SessionStore sessionStore
    ) {
        this.authService = authService;
        this.sessionStore = sessionStore;
    }

    public void start() throws Exception {

        String keystorePath =
                System.getenv().getOrDefault(
                        "PIXEL_CHAT_KEYSTORE",
                        "data/pixel-chat-server.p12"
                );

        String keystorePassword =
                System.getenv().getOrDefault(
                        "PIXEL_CHAT_KEYSTORE_PASSWORD",
                        "pixelchatdev"
                );

        SSLServerSocket serverSocket =
                TlsConfig.createServerSocket(
                        PORT,
                        Path.of(keystorePath),
                        keystorePassword
                );

        System.out.println("TLS: ENABLED");
        System.out.println("Protocol: TLSv1.3");
        System.out.println("API: HTTPS-like encrypted TCP");
        System.out.println("Port: " + PORT);
        System.out.println(
                "Server 10.0 готов принимать TLS-соединения."
        );

        while (true) {

            SSLSocket client =
                    (SSLSocket) serverSocket.accept();

            pool.submit(
                    () -> handleClient(client)
            );
        }
    }

    private void handleClient(SSLSocket client) {

        String address =
                client.getInetAddress()
                        .getHostAddress();

        try (client) {

            client.setEnabledProtocols(
                    new String[]{"TLSv1.3"}
            );

            client.startHandshake();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    client.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

            BufferedWriter writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client.getOutputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

            send(writer, "PIXEL_CHAT_SERVER|10.0");
            send(writer, "TLS|1.3");
            send(writer, "READY");

            String request;

            while ((request = reader.readLine()) != null) {

                if (!rateLimiter.allow(address)) {
                    send(writer, "ERROR|RATE_LIMIT");
                    break;
                }

                if (request.length() >
                        MAX_REQUEST_LENGTH) {

                    send(
                            writer,
                            "ERROR|REQUEST_TOO_LARGE"
                    );
                    break;
                }

                String response =
                        processRequest(request);

                send(writer, response);

                if ("QUIT".equalsIgnoreCase(
                        request.trim()
                )) {
                    break;
                }
            }

        } catch (Exception ignored) {
            // Секретные данные не логируем.
        }
    }

    private String processRequest(String request) {

        if (request == null ||
                request.isBlank()) {

            return "ERROR|EMPTY_REQUEST";
        }

        String[] parts =
                request.split("\\\\|", 4);

        String command =
                parts[0].trim().toUpperCase();

        try {

            switch (command) {

                case "PING":
                    return "PONG";

                case "STATUS":
                    return "OK|SERVER_10.0|ONLINE|TLS";

                case "REGISTER":

                    if (parts.length != 4) {
                        return "ERR|BAD_REQUEST";
                    }

                    return authService.startRegistration(
                            parts[1],
                            parts[2],
                            parts[3]
                    );

                case "VERIFY":

                    if (parts.length != 3) {
                        return "ERR|BAD_REQUEST";
                    }

                    return authService.verifyRegistration(
                            parts[1],
                            parts[2]
                    );

                case "LOGIN":

                    if (parts.length != 3) {
                        return "ERR|BAD_REQUEST";
                    }

                    Optional<User> user =
                            authService.authenticate(
                                    parts[1],
                                    parts[2]
                            );

                    if (user.isEmpty()) {
                        return "ERR|INVALID_LOGIN";
                    }

                    String token =
                            sessionStore.create(
                                    user.get().getId()
                            );

                    return "OK|LOGIN|" + token;

                case "SESSION":

                    if (parts.length != 2) {
                        return "ERR|BAD_REQUEST";
                    }

                    Optional<String> userId =
                            sessionStore.getUserId(
                                    parts[1]
                            );

                    if (userId.isEmpty()) {
                        return "ERR|INVALID_SESSION";
                    }

                    return "OK|SESSION|" + userId.get();

                case "LOGOUT":

                    if (parts.length != 2) {
                        return "ERR|BAD_REQUEST";
                    }

                    sessionStore.invalidate(
                            parts[1]
                    );

                    return "OK|LOGGED_OUT";

                case "QUIT":
                    return "BYE";

                default:
                    return "ERROR|UNKNOWN_COMMAND";
            }

        } catch (IllegalStateException e) {

            return "ERR|" + safeError(
                    e.getMessage()
            );

        } catch (Exception e) {

            return "ERROR|SERVER_ERROR";
        }
    }

    private static String safeError(String message) {

        if (message == null ||
                message.isBlank()) {

            return "REQUEST_FAILED";
        }

        return message
                .replace("|", "_")
                .replace("\n", "_")
                .replace("\r", "_");
    }

    private static void send(
            BufferedWriter writer,
            String message
    ) throws IOException {

        writer.write(message);
        writer.newLine();
        writer.flush();
    }
}
"""
}


def update_file(filename, content):
    target = SRC / filename

    if target.exists():
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        backup = SRC / f"{filename}.{timestamp}.bak"
        shutil.copy2(target, backup)
        print(f"[BACKUP] {backup.name}")

    target.write_text(content, encoding="utf-8")
    print(f"[UPDATED] {filename}")


def remove_old_java_backups():
    removed = 0

    for file in SRC.glob("*.backup_*.java"):
        file.unlink()
        print(f"[REMOVE OLD BACKUP] {file.name}")
        removed += 1

    return removed


print("================================")
print(" PIXEL CHAT SERVER 10.0 UPDATER")
print("================================")

SRC.mkdir(parents=True, exist_ok=True)

remove_old_java_backups()

for filename, content in FILES.items():
    update_file(filename, content)

print()
print("================================")
print(" ОБНОВЛЕНИЕ ЗАВЕРШЕНО")
print("================================")
print()
print("Теперь выполни:")
print("gradle compileJava")
