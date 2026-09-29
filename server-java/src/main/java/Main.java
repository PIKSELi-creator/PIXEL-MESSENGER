import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final int PORT = 5000;

    private static final Path DATA_DIR =
            Paths.get("server-java/data");

    private static final Path USERS_FILE =
            DATA_DIR.resolve("users.txt");

    private static final Path MESSAGES_FILE =
            DATA_DIR.resolve("messages.txt");

    private static final Map<String, User> users =
            new ConcurrentHashMap<>();

    private static final List<Message> messages =
            Collections.synchronizedList(new ArrayList<>());

    private static final Map<String, ClientHandler> online =
            new ConcurrentHashMap<>();

    private static final Object DATA_LOCK = new Object();

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("       PIXEL CHAT SERVER");
        System.out.println("          Java v0.2");
        System.out.println("================================");

        try {
            Files.createDirectories(DATA_DIR);
            loadUsers();
            loadMessages();

            System.out.println("Пользователей загружено: " + users.size());
            System.out.println("Сообщений загружено: " + messages.size());

            try (ServerSocket serverSocket =
                         new ServerSocket(PORT)) {

                System.out.println("Сервер запущен.");
                System.out.println("Порт: " + PORT);
                System.out.println("Ожидание подключений...");

                ExecutorService pool =
                        Executors.newCachedThreadPool();

                while (true) {
                    Socket client = serverSocket.accept();

                    System.out.println(
                            "Новое подключение: " +
                            client.getInetAddress().getHostAddress()
                    );

                    pool.execute(new ClientHandler(client));
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "Ошибка сервера: " + e.getMessage()
            );
        }
    }

    private static void loadUsers() {
        if (!Files.exists(USERS_FILE)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(
                    USERS_FILE, StandardCharsets.UTF_8)) {

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", 2);

                if (parts.length == 2) {
                    users.put(
                            parts[0],
                            new User(parts[0], parts[1])
                    );
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Ошибка загрузки пользователей: " +
                    e.getMessage()
            );
        }
    }

    private static void loadMessages() {
        if (!Files.exists(MESSAGES_FILE)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(
                    MESSAGES_FILE, StandardCharsets.UTF_8)) {

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", 4);

                if (parts.length == 4) {
                    String text = new String(
                            Base64.getDecoder().decode(parts[3]),
                            StandardCharsets.UTF_8
                    );

                    messages.add(
                            new Message(
                                    parts[0],
                                    parts[1],
                                    parts[2],
                                    text
                            )
                    );
                }
            }
        } catch (Exception e) {
            System.out.println(
                    "Ошибка загрузки сообщений: " +
                    e.getMessage()
            );
        }
    }

    private static void saveUser(User user) {
        synchronized (DATA_LOCK) {
            try {
                Files.createDirectories(DATA_DIR);

                Files.writeString(
                        USERS_FILE,
                        user.login + "|" + user.passwordHash +
                                System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );

            } catch (IOException e) {
                System.out.println(
                        "Ошибка сохранения пользователя: " +
                        e.getMessage()
                );
            }
        }
    }

    private static void saveMessage(Message message) {
        synchronized (DATA_LOCK) {
            try {
                String encoded =
                        Base64.getEncoder().encodeToString(
                                message.text.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        );

                String line =
                        message.timestamp + "|" +
                        message.from + "|" +
                        message.to + "|" +
                        encoded +
                        System.lineSeparator();

                Files.writeString(
                        MESSAGES_FILE,
                        line,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );

            } catch (IOException e) {
                System.out.println(
                        "Ошибка сохранения сообщения: " +
                        e.getMessage()
                );
            }
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean validLogin(String login) {
        return login != null &&
                login.length() >= 3 &&
                login.length() <= 20 &&
                login.matches("[A-Za-z0-9_]+");
    }

    static class User {
        String login;
        String passwordHash;

        User(String login, String passwordHash) {
            this.login = login;
            this.passwordHash = passwordHash;
        }
    }

    static class Message {
        String timestamp;
        String from;
        String to;
        String text;

        Message(
                String timestamp,
                String from,
                String to,
                String text
        ) {
            this.timestamp = timestamp;
            this.from = from;
            this.to = to;
            this.text = text;
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        private BufferedReader in;
        private PrintWriter out;

        private String currentUser = null;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private void send(String text) {
            synchronized (this) {
                out.println(text);
            }
        }

        @Override
        public void run() {
            try (
                    Socket client = socket;
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            client.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );
                    PrintWriter writer =
                            new PrintWriter(
                                    new OutputStreamWriter(
                                            client.getOutputStream(),
                                            StandardCharsets.UTF_8
                                    ),
                                    true
                            )
            ) {
                in = reader;
                out = writer;

                send("PIXEL_CHAT_SERVER_OK");
                send("Добро пожаловать на PIXEL CHAT.");
                send("READY");

                String request;

                while ((request = in.readLine()) != null) {
                    System.out.println(
                            "[" +
                            client.getInetAddress()
                                  .getHostAddress() +
                            "] " +
                            request
                    );

                    if (request.equals("PING")) {
                        send("PONG");

                    } else if (request.startsWith("REGISTER|")) {
                        register(request);

                    } else if (request.startsWith("LOGIN|")) {
                        login(request);

                    } else if (request.equals("LIST")) {
                        listUsers();

                    } else if (request.startsWith("SEND|")) {
                        sendMessage(request);

                    } else if (request.startsWith("HISTORY|")) {
                        history(request);

                    } else if (request.equals("LOGOUT")) {
                        logout();

                    } else if (request.equals("QUIT")) {
                        send("BYE");
                        break;

                    } else {
                        send("ERR|UNKNOWN_COMMAND");
                    }
                }

            } catch (IOException e) {
                System.out.println(
                        "Клиент отключился: " +
                        e.getMessage()
                );

            } finally {
                disconnect();
            }
        }

        private void register(String request) {
            String[] parts = request.split("\\|", 3);

            if (parts.length != 3) {
                send("ERR|BAD_REGISTER");
                return;
            }

            String login = parts[1].trim();
            String password = parts[2];

            if (!validLogin(login)) {
                send("ERR|BAD_LOGIN");
                return;
            }

            if (password.length() < 4) {
                send("ERR|PASSWORD_TOO_SHORT");
                return;
            }

            if (users.containsKey(login)) {
                send("ERR|USER_EXISTS");
                return;
            }

            User user = new User(
                    login,
                    hashPassword(password)
            );

            users.put(login, user);
            saveUser(user);

            send("OK|REGISTERED");
        }

        private void login(String request) {
            String[] parts = request.split("\\|", 3);

            if (parts.length != 3) {
                send("ERR|BAD_LOGIN");
                return;
            }

            String login = parts[1].trim();
            String password = parts[2];

            User user = users.get(login);

            if (user == null) {
                send("ERR|USER_NOT_FOUND");
                return;
            }

            String hash = hashPassword(password);

            if (!hash.equals(user.passwordHash)) {
                send("ERR|WRONG_PASSWORD");
                return;
            }

            if (online.containsKey(login)) {
                send("ERR|ALREADY_ONLINE");
                return;
            }

            currentUser = login;
            online.put(login, this);

            send("OK|LOGIN_SUCCESS");
        }

        private void listUsers() {
            if (currentUser == null) {
                send("ERR|NOT_LOGGED_IN");
                return;
            }

            List<String> sorted =
                    new ArrayList<>(users.keySet());

            Collections.sort(sorted);

            for (String login : sorted) {
                boolean isOnline =
                        online.containsKey(login);

                send(
                        "USER|" +
                        login +
                        "|" +
                        (isOnline ? "ONLINE" : "OFFLINE")
                );
            }

            send("END");
        }

        private void sendMessage(String request) {
            if (currentUser == null) {
                send("ERR|NOT_LOGGED_IN");
                return;
            }

            String[] parts = request.split("\\|", 3);

            if (parts.length != 3) {
                send("ERR|BAD_MESSAGE");
                return;
            }

            String to = parts[1];
            String text = parts[2];

            if (!users.containsKey(to)) {
                send("ERR|RECIPIENT_NOT_FOUND");
                return;
            }

            if (text.isBlank()) {
                send("ERR|EMPTY_MESSAGE");
                return;
            }

            text = text.replace('\n', ' ')
                       .replace('\r', ' ');

            String timestamp =
                    String.valueOf(System.currentTimeMillis());

            Message message =
                    new Message(
                            timestamp,
                            currentUser,
                            to,
                            text
                    );

            messages.add(message);
            saveMessage(message);

            send("OK|MESSAGE_SAVED");
        }

        private void history(String request) {
            if (currentUser == null) {
                send("ERR|NOT_LOGGED_IN");
                return;
            }

            String[] parts = request.split("\\|", 2);

            if (parts.length != 2) {
                send("ERR|BAD_HISTORY");
                return;
            }

            String other = parts[1];

            for (Message message : messages) {
                boolean match =
                        (message.from.equals(currentUser)
                                && message.to.equals(other))
                        ||
                        (message.from.equals(other)
                                && message.to.equals(currentUser));

                if (match) {
                    send(
                            "MSG|" +
                            message.timestamp +
                            "|" +
                            message.from +
                            "|" +
                            message.to +
                            "|" +
                            message.text
                    );
                }
            }

            send("END");
        }

        private void logout() {
            if (currentUser != null) {
                online.remove(currentUser);
                currentUser = null;
            }

            send("OK|LOGOUT");
        }

        private void disconnect() {
            if (currentUser != null) {
                online.remove(currentUser);
                currentUser = null;
            }
        }
    }
}
