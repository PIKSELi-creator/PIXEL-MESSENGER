#include <arpa/inet.h>
#include <fstream>
#include <iostream>
#include <netinet/in.h>
#include <sstream>
#include <string>
#include <sys/socket.h>
#include <unistd.h>

bool sendRequest(const std::string& request, std::string& response) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);

    if (sock < 0) {
        return false;
    }

    sockaddr_in server{};
    server.sin_family = AF_INET;
    server.sin_port = htons(5001);

    if (inet_pton(AF_INET, "127.0.0.1", &server.sin_addr) <= 0) {
        close(sock);
        return false;
    }

    if (connect(sock, (sockaddr*)&server, sizeof(server)) < 0) {
        close(sock);
        return false;
    }

    std::string data = request + "\n";

    send(sock, data.c_str(), data.size(), 0);

    char buffer[8192];
    ssize_t received = recv(sock, buffer, sizeof(buffer) - 1, 0);

    if (received <= 0) {
        close(sock);
        return false;
    }

    buffer[received] = '\0';
    response = buffer;

    close(sock);
    return true;
}

int main() {

    const std::string username = "testuser";
    const std::string publicKeyFile =
        "crypto-keys/testuser_public.pem";

    std::ifstream file(publicKeyFile);

    if (!file) {
        std::cerr << "Не удалось открыть public key\n";
        return 1;
    }

    std::stringstream buffer;
    buffer << file.rdbuf();

    std::string publicKey = buffer.str();

    // PEM содержит переводы строк.
    // Для нашего простого протокола заменяем их пробелами.
    for (char& c : publicKey) {
        if (c == '\n' || c == '\r') {
            c = ' ';
        }
    }

    std::string response;

    std::cout << "=== PIXEL CHAT X25519 KEY REGISTER ===\n\n";
    std::cout << "Пользователь: @" << username << "\n";
    std::cout << "Отправляем настоящий X25519 public key...\n";

    std::string request =
        "KEY_REGISTER|" +
        username +
        "|" +
        publicKey;

    if (!sendRequest(request, response)) {
        std::cerr << "Ошибка подключения к crypto server\n";
        return 1;
    }

    std::cout << "Server: " << response;

    return 0;
}
