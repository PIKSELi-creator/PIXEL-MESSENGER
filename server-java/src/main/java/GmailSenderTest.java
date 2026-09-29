public class GmailSenderTest {

    public static void main(String[] args) throws Exception {
        String recipient = "aminosav898@gmail.com";
        String code = "483921";

        System.out.println("Отправляем тестовое письмо...");
        GmailSender.sendVerificationCode(recipient, code);
        System.out.println("Тестовое письмо успешно отправлено!");
    }
}
