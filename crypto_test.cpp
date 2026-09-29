#include <arpa/inet.h>
#include <cstring>
#include <iostream>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

bool sendRequest(const std::string& request, std::string& response) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);

    if (sock < 0) {
        std::cerr << "socket() failed\n";
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
        std::cerr << "connect() failed\n";
        close(sock);
        return false;
    }

    std::string data = request + "\n";

    send(sock, data.c_str(), data.size(), 0);

    char buffer[4096];
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

    std::cout << "=== PIXEL CHAT CRYPTO TEST ===\n\n";

    std::string response;

    std::cout << "[1] Register public key...\n";

    if (!sendRequest(
            "KEY_REGISTER|testuser|TEST_PUBLIC_KEY_123456",
            response)) {
        return 1;
    }

    std::cout << "Server: " << response;

    std::cout << "\n[2] Get public key...\n";

    if (!sendRequest(
            "KEY_GET|testuser",
            response)) {
        return 1;
    }

    std::cout << "Server: " << response;

    std::cout << "\n=== TEST FINISHED ===\n";

    return 0;
}
