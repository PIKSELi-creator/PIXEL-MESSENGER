import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

public class Main {
    private static final int PORT = 5001;
    private static final int MAX_REQUEST_LENGTH = 16384;

    public static void main(String[] args) throws Exception {
        System.out.println("=================================");
        System.out.println("      PIXEL CHAT CRYPTO SERVER");
        System.out.println("             E2EE 1.1");
        System.out.println("=================================");
        System.out.println("Private keys: NEVER stored");
        System.out.println("Public keys: PERSISTENT");
        System.out.println("Ciphertext: OPAQUE");
        System.out.println("Bind: 127.0.0.1:" + PORT);
        System.out.println();

        PublicKeyStore keyStore = new PublicKeyStore(
                Path.of("data/public_keys.dat"));

        try (ServerSocket serverSocket = new ServerSocket(
                PORT, 100, InetAddress.getByName("127.0.0.1"))) {
            System.out.println("Crypto server listening on 127.0.0.1:" + PORT);
            while (true) {
                Socket client = serverSocket.accept();
                Thread.startVirtualThread(() -> handleClient(client, keyStore));
            }
        }
    }

    private static void handleClient(Socket socket, PublicKeyStore keyStore) {
        try (Socket client = socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     client.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     client.getOutputStream(), StandardCharsets.UTF_8))) {

            client.setSoTimeout(15000);
            String request = reader.readLine();
            if (request == null) return;
            if (request.length() > MAX_REQUEST_LENGTH) {
                send(writer, "ERROR|REQUEST_TOO_LARGE");
                return;
            }

            String[] parts = request.split("\\|", 3);

            if (parts.length == 3 && parts[0].equals("KEY_REGISTER")) {
                keyStore.put(parts[1], parts[2]);
                send(writer, "OK|KEY_REGISTERED");
                return;
            }

            if (parts.length == 2 && parts[0].equals("KEY_GET")) {
                Optional<String> key = keyStore.get(parts[1]);
                send(writer, key.isEmpty()
                        ? "ERROR|KEY_NOT_FOUND"
                        : "PUBLIC_KEY|" + parts[1] + "|" + key.get());
                return;
            }

            if (parts.length == 2 && parts[0].equals("KEY_DELETE")) {
                send(writer, keyStore.remove(parts[1])
                        ? "OK|KEY_DELETED"
                        : "ERROR|KEY_NOT_FOUND");
                return;
            }

            if (parts.length == 1 && parts[0].equals("STATUS")) {
                send(writer, "OK|CRYPTO_SERVER|ONLINE|KEYS=" + keyStore.size());
                return;
            }

            send(writer, "ERROR|UNKNOWN_COMMAND");
        } catch (Exception ignored) {
            // Do not log private keys or ciphertext.
        }
    }

    private static void send(BufferedWriter writer, String response)
            throws IOException {
        writer.write(response);
        writer.newLine();
        writer.flush();
    }
}
