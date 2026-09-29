import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;
import java.util.Base64;

public class GmailSender {

    private static final String APPLICATION_NAME = "PIXEL CHAT";

    private static final GsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    /*
     * OAuth credentials PIXEL CHAT.
     * Секретный JSON НЕ хранится в Git.
     */
    private static final Path CREDENTIALS =
            Path.of(
                    System.getenv().getOrDefault(
                            "PIXEL_CHAT_GMAIL_CREDENTIALS",
                            "../server-java/client_secret_493049226839-8dpjv40tkbu201rquaf3gos2iun1tkfq.apps.googleusercontent.com.json"
                    )
            );

    /*
     * Отдельное хранилище OAuth-токена.
     *
     * Здесь будет храниться авторизация именно
     * аккаунта PIXEL CHAT.
     */
    private static final Path TOKENS =
            Path.of(
                    System.getenv().getOrDefault(
                            "PIXEL_CHAT_GMAIL_TOKENS",
                            "../server-java/gmail-tokens-pixelchat"
                    )
            );

    /*
     * ОТДЕЛЬНЫЙ Gmail PIXEL CHAT
     */
    private static final String SENDER_EMAIL =
            "pixelchat.official@gmail.com";

    private static final String SENDER_NAME =
            "PIXEL CHAT";

    private static final String GMAIL_SEND_SCOPE =
            "https://www.googleapis.com/auth/gmail.send";


    public static Gmail getGmailService() throws Exception {

        NetHttpTransport transport =
                GoogleNetHttpTransport.newTrustedTransport();

        if (!Files.exists(CREDENTIALS)) {
            throw new FileNotFoundException(
                    "Не найден OAuth JSON: " + CREDENTIALS
            );
        }

        Files.createDirectories(TOKENS);

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY,
                        Files.newBufferedReader(CREDENTIALS)
                );

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        transport,
                        JSON_FACTORY,
                        clientSecrets,
                        Collections.singletonList(GMAIL_SEND_SCOPE)
                )
                .setDataStoreFactory(
                        new FileDataStoreFactory(
                                TOKENS.toFile()
                        )
                )
                .setAccessType("offline")
                .build();

        Credential credential =
                new AuthorizationCodeInstalledApp(
                        flow,
                        new LocalServerReceiver.Builder()
                                .setPort(8888)
                                .build()
                ).authorize("pixel-chat-sender");

        return new Gmail.Builder(
                transport,
                JSON_FACTORY,
                credential
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }


    public static void sendVerificationCode(
            String recipient,
            String code
    ) throws Exception {

        Gmail service = getGmailService();

        Properties properties = new Properties();

        Session session =
                Session.getInstance(properties);

        MimeMessage email =
                new MimeMessage(session);

        /*
         * Теперь письмо отправляется
         * от отдельного PIXEL CHAT Gmail.
         */
        email.setFrom(
                new InternetAddress(
                        SENDER_EMAIL,
                        SENDER_NAME
                )
        );

        email.setRecipients(
                jakarta.mail.Message.RecipientType.TO,
                InternetAddress.parse(recipient)
        );

        email.setSubject(
                "PIXEL CHAT — Код подтверждения",
                "UTF-8"
        );

        email.setText(
                "PIXEL CHAT\n\n" +
                "Здравствуйте!\n\n" +
                "Ваш код подтверждения:\n\n" +
                code + "\n\n" +
                "Код действует 10 минут.\n\n" +
                "Если вы не регистрировались в PIXEL CHAT, " +
                "просто проигнорируйте это письмо.\n\n" +
                "С уважением,\n" +
                "Команда PIXEL CHAT",
                "UTF-8"
        );

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        email.writeTo(buffer);

        String encodedEmail =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                buffer.toByteArray()
                        );

        Message message =
                new Message();

        message.setRaw(encodedEmail);

        service.users()
                .messages()
                .send("me", message)
                .execute();

        System.out.println(
                "Verification email sent by PIXEL CHAT: "
                        + SENDER_EMAIL
                        + " -> "
                        + recipient
        );
    }
}
