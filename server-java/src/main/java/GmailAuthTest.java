public class GmailAuthTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Запускаем авторизацию PIXEL CHAT...");

        GmailSender.getGmailService();

        System.out.println("OAuth авторизация успешно завершена!");
        System.out.println("Токен сохранён локально.");
    }
}
