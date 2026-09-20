#include <iostream>
#include <string>
#include <vector>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>

using namespace std;

const char* SERVER_IP = "127.0.0.1";
const int SERVER_PORT = 5000;

bool sendLine(int socketFd, const string& text) {
    string data = text + "\n";

    size_t sentTotal = 0;

    while (sentTotal < data.size()) {
        ssize_t sent = send(
            socketFd,
            data.c_str() + sentTotal,
            data.size() - sentTotal,
            0
        );

        if (sent <= 0) {
            return false;
        }

        sentTotal += sent;
    }

    return true;
}

bool receiveLine(int socketFd, string& result) {
    result.clear();

    char c;

    while (true) {
        ssize_t received = recv(
            socketFd,
            &c,
            1,
            0
        );

        if (received <= 0) {
            return false;
        }

        if (c == '\n') {
            break;
        }

        if (c != '\r') {
            result += c;
        }

        if (result.size() > 8192) {
            return false;
        }
    }

    return true;
}

vector<string> splitFirst(
    const string& text,
    int count
) {
    vector<string> result;
    size_t start = 0;

    for (int i = 0; i < count - 1; i++) {
        size_t pos = text.find('|', start);

        if (pos == string::npos) {
            result.push_back(text.substr(start));
            return result;
        }

        result.push_back(
            text.substr(start, pos - start)
        );

        start = pos + 1;
    }

    result.push_back(text.substr(start));

    return result;
}

bool connectToServer(int& socketFd) {
    socketFd = socket(
        AF_INET,
        SOCK_STREAM,
        0
    );

    if (socketFd < 0) {
        return false;
    }

    sockaddr_in serverAddress{};

    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port =
            htons(SERVER_PORT);

    if (inet_pton(
            AF_INET,
            SERVER_IP,
            &serverAddress.sin_addr
        ) <= 0) {

        close(socketFd);
        return false;
    }

    if (connect(
            socketFd,
            reinterpret_cast<sockaddr*>(
                &serverAddress
            ),
            sizeof(serverAddress)
        ) < 0) {

        close(socketFd);
        return false;
    }

    return true;
}

bool receiveServerAnswer(
    int socketFd,
    string& answer
) {
    return receiveLine(
        socketFd,
        answer
    );
}

void registerAccount(int socketFd) {
    string login;
    string password;

    cout << endl;
    cout << "=== РЕГИСТРАЦИЯ ===" << endl;

    cout << "Логин: ";
    getline(cin, login);

    cout << "Пароль: ";
    getline(cin, password);

    sendLine(
        socketFd,
        "REGISTER|" + login + "|" + password
    );

    string response;

    if (!receiveServerAnswer(
            socketFd,
            response
        )) {

        cout << "Сервер отключился." << endl;
        return;
    }

    if (response == "OK|REGISTERED") {
        cout << endl;
        cout << "[OK] Аккаунт создан!" << endl;
    } else {
        cout << endl;
        cout << "SERVER: " << response << endl;
    }
}

bool loginAccount(
    int socketFd,
    string& currentUser
) {
    string login;
    string password;

    cout << endl;
    cout << "=== ВХОД ===" << endl;

    cout << "Логин: ";
    getline(cin, login);

    cout << "Пароль: ";
    getline(cin, password);

    sendLine(
        socketFd,
        "LOGIN|" + login + "|" + password
    );

    string response;

    if (!receiveServerAnswer(
            socketFd,
            response
        )) {

        cout << "Сервер отключился." << endl;
        return false;
    }

    if (response == "OK|LOGIN_SUCCESS") {
        currentUser = login;

        cout << endl;
        cout << "[OK] Вход выполнен!" << endl;
        cout << "Добро пожаловать, "
             << currentUser
             << "!" << endl;

        return true;
    }

    cout << endl;
    cout << "SERVER: " << response << endl;

    return false;
}

void listUsers(int socketFd) {
    sendLine(socketFd, "LIST");

    cout << endl;
    cout << "=== ПОЛЬЗОВАТЕЛИ ===" << endl;

    string response;

    while (receiveLine(
        socketFd,
        response
    )) {
        if (response == "END") {
            break;
        }

        vector<string> parts =
                splitFirst(response, 3);

        if (parts.size() == 3 &&
            parts[0] == "USER") {

            cout << "- "
                 << parts[1]
                 << " ["
                 << parts[2]
                 << "]"
                 << endl;
        }
    }
}

void sendMessage(int socketFd) {
    string to;
    string text;

    cout << endl;
    cout << "Получатель: ";
    getline(cin, to);

    cout << "Сообщение: ";
    getline(cin, text);

    sendLine(
        socketFd,
        "SEND|" + to + "|" + text
    );

    string response;

    if (receiveLine(
            socketFd,
            response
        )) {

        if (response == "OK|MESSAGE_SAVED") {
            cout << "[OK] Сообщение сохранено!" << endl;
        } else {
            cout << "SERVER: "
                 << response
                 << endl;
        }
    }
}

void showHistory(int socketFd) {
    string other;

    cout << endl;
    cout << "История с пользователем: ";
    getline(cin, other);

    sendLine(
        socketFd,
        "HISTORY|" + other
    );

    cout << endl;
    cout << "=== ИСТОРИЯ ===" << endl;

    string response;
    bool found = false;

    while (receiveLine(
        socketFd,
        response
    )) {
        if (response == "END") {
            break;
        }

        vector<string> parts =
                splitFirst(response, 5);

        if (parts.size() == 5 &&
            parts[0] == "MSG") {

            cout << "[" << parts[1] << "] "
                 << parts[2]
                 << " -> "
                 << parts[3]
                 << ": "
                 << parts[4]
                 << endl;

            found = true;
        }
    }

    if (!found) {
        cout << "Сообщений пока нет." << endl;
    }
}

void messengerMenu(
    int socketFd,
    string& currentUser
) {
    while (true) {
        cout << endl;
        cout << "================================" << endl;
        cout << "        PIXEL CHAT" << endl;
        cout << "Пользователь: " << currentUser << endl;
        cout << "================================" << endl;
        cout << "1. Пользователи" << endl;
        cout << "2. Отправить сообщение" << endl;
        cout << "3. История чата" << endl;
        cout << "4. Выйти из аккаунта" << endl;
        cout << "5. Закрыть PIXEL CHAT" << endl;
        cout << "================================" << endl;
        cout << "> ";

        string choice;
        getline(cin, choice);

        if (choice == "1") {
            listUsers(socketFd);

        } else if (choice == "2") {
            sendMessage(socketFd);

        } else if (choice == "3") {
            showHistory(socketFd);

        } else if (choice == "4") {
            sendLine(socketFd, "LOGOUT");

            string response;

            if (receiveLine(
                    socketFd,
                    response
                )) {

                cout << "SERVER: "
                     << response
                     << endl;
            }

            currentUser.clear();
            return;

        } else if (choice == "5") {
            sendLine(socketFd, "QUIT");

            string response;

            receiveLine(
                socketFd,
                response
            );

            return;

        } else {
            cout << "Неизвестная команда." << endl;
        }
    }
}

int main() {
    cout << "================================" << endl;
    cout << "        PIXEL MESSENGER" << endl;
    cout << "          C++ Client" << endl;
    cout << "================================" << endl;

    int socketFd;

    cout << endl;
    cout << "Подключение к Java-серверу..." << endl;

    if (!connectToServer(socketFd)) {
        cout << endl;
        cout << "[ERROR] Не удалось подключиться." << endl;
        cout << "Проверь Main.java и порт 5000." << endl;
        return 1;
    }

    cout << "[OK] Сервер подключен!" << endl;

    string response;

    receiveLine(socketFd, response);
    cout << "SERVER: " << response << endl;

    receiveLine(socketFd, response);
    cout << "SERVER: " << response << endl;

    receiveLine(socketFd, response);

    bool running = true;

    while (running) {
        cout << endl;
        cout << "================================" << endl;
        cout << "        PIXEL CHAT" << endl;
        cout << "================================" << endl;
        cout << "1. Регистрация" << endl;
        cout << "2. Вход" << endl;
        cout << "3. Выход" << endl;
        cout << "================================" << endl;
        cout << "> ";

        string choice;
        getline(cin, choice);

        if (choice == "1") {
            registerAccount(socketFd);

        } else if (choice == "2") {
            string currentUser;

            if (loginAccount(
                    socketFd,
                    currentUser
                )) {

                messengerMenu(
                    socketFd,
                    currentUser
                );

                if (currentUser.empty()) {
                    continue;
                }

                running = false;
            }

        } else if (choice == "3") {
            sendLine(socketFd, "QUIT");

            receiveLine(
                socketFd,
                response
            );

            running = false;

        } else {
            cout << "Неизвестная команда." << endl;
        }
    }

    close(socketFd);

    cout << endl;
    cout << "Соединение закрыто." << endl;

    return 0;
}
