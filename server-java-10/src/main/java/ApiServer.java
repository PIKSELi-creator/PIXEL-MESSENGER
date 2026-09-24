import javax.net.ssl.SSLServerSocket;
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
    private static final int MAX_CONNECTIONS = 50;
    private static final int SOCKET_TIMEOUT_MS = 30_000;

    private final AuthService authService;
    private final SessionStore sessionStore;
    private final MessageService messageService;

    private final RateLimiter rateLimiter =
            new RateLimiter();

    private final java.util.concurrent.Semaphore connectionLimiter =
            new java.util.concurrent.Semaphore(
                    MAX_CONNECTIONS
            );

    private final ExecutorService pool =
            Executors.newFixedThreadPool(
                    MAX_CONNECTIONS
            );

    public ApiServer(
            AuthService authService,
            SessionStore sessionStore,
            MessageService messageService
    ) {
        this.authService = authService;
        this.sessionStore = sessionStore;
        this.messageService = messageService;
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

            if (!connectionLimiter.tryAcquire()) {

                client.close();
                continue;
            }

            pool.submit(
                    () -> {
                        try {
                            handleClient(client);
                        } finally {
                            connectionLimiter.release();
                        }
                    }
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

            client.setSoTimeout(
                    SOCKET_TIMEOUT_MS
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
                request.split("\\|", 4);

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

                case "SEND_MESSAGE":
                    if (parts.length != 4) {
                        return "ERR|BAD_REQUEST";
                    }
                    return messageService.sendMessage(parts[1], parts[2], parts[3]);

                case "GET_MESSAGES":
                    if (parts.length != 3) {
                        return "ERR|BAD_REQUEST";
                    }
                    return messageService.getMessages(parts[1], parts[2]);

                case "MARK_READ":
                    if (parts.length != 3) {
                        return "ERR|BAD_REQUEST";
                    }
                    return messageService.markRead(parts[1], parts[2]);

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
                .replace("\\n", "_")
                .replace("\\r", "_");
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
