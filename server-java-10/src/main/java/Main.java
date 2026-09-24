import java.nio.file.Path;

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

            MessageStore messageStore =
                    new MessageStore(
                            Path.of("data/messages.dat")
                    );

            MessageService messageService =
                    new MessageService(
                            userStore,
                            sessionStore,
                            messageStore
                    );

            System.out.println("UserStore: ONLINE");
            System.out.println("AuthService: ONLINE");
            System.out.println("VerificationService: ONLINE");
            System.out.println("SessionStore: ONLINE");

            ApiServer apiServer =
                    new ApiServer(
                            authService,
                            sessionStore,
                            messageService
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
