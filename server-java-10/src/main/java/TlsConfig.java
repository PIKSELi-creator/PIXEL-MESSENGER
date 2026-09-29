import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;

public class TlsConfig {

    private TlsConfig() {
    }

    public static SSLServerSocket createServerSocket(
            int port,
            Path keystorePath,
            String keystorePassword
    ) throws Exception {

        KeyStore keyStore =
                KeyStore.getInstance("PKCS12");

        try (FileInputStream input =
                     new FileInputStream(
                             keystorePath.toFile()
                     )) {

            keyStore.load(
                    input,
                    keystorePassword.toCharArray()
            );
        }

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory
                                .getDefaultAlgorithm()
                );

        keyManagerFactory.init(
                keyStore,
                keystorePassword.toCharArray()
        );

        SSLContext sslContext =
                SSLContext.getInstance("TLS");

        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                null,
                null
        );

        SSLServerSocket serverSocket =
                (SSLServerSocket)
                        sslContext
                                .getServerSocketFactory()
                                .createServerSocket(port);

        serverSocket.setEnabledProtocols(
                new String[]{"TLSv1.3"}
        );

        return serverSocket;
    }
}
