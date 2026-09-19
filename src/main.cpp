#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <limits>

using namespace std;

struct User {
    string login;
    string password;
};

struct Message {
    string from;
    string to;
    string text;
};

vector<User> users;
vector<Message> messages;
string currentUser;

// ===============================
// Загрузка пользователей
// ===============================
void loadUsers() {
    ifstream file("users.txt");

    User user;

    while (file >> user.login >> user.password) {
        users.push_back(user);
    }

    file.close();
}

// ===============================
// Сохранение пользователей
// ===============================
void saveUsers() {
    ofstream file("users.txt");

    for (const User& user : users) {
        file << user.login << " " << user.password << "\n";
    }

    file.close();
}

// ===============================
// Загрузка сообщений
// ===============================
void loadMessages() {
    ifstream file("messages.txt");

    string from;
    string to;
    string text;

    while (getline(file, from, '|')) {
        getline(file, to, '|');
        getline(file, text);

        if (!from.empty() && !to.empty()) {
            messages.push_back({from, to, text});
        }
    }

    file.close();
}

// ===============================
// Сохранение сообщений
// ===============================
void saveMessages() {
    ofstream file("messages.txt");

    for (const Message& message : messages) {
        file << message.from << "|"
             << message.to << "|"
             << message.text << "\n";
    }

    file.close();
}

// ===============================
// Проверка существования пользователя
// ===============================
bool userExists(const string& login) {
    for (const User& user : users) {
        if (user.login == login) {
            return true;
        }
    }

    return false;
}

// ===============================
// Регистрация
// ===============================
void registerUser() {
    string login;
    string password;

    cout << "\n========== РЕГИСТРАЦИЯ ==========\n";

    cout << "Введите логин: ";
    cin >> login;

    if (userExists(login)) {
        cout << "Пользователь с таким логином уже существует.\n";
        return;
    }

    cout << "Введите пароль: ";
    cin >> password;

    users.push_back({login, password});
    saveUsers();

    cout << "Аккаунт успешно создан.\n";
}

// ===============================
// Вход
// ===============================
bool loginUser() {
    string login;
    string password;

    cout << "\n============ ВХОД ============\n";

    cout << "Логин: ";
    cin >> login;

    cout << "Пароль: ";
    cin >> password;

    for (const User& user : users) {
        if (user.login == login &&
            user.password == password) {

            currentUser = login;

            cout << "\nДобро пожаловать, "
                 << currentUser << "!\n";

            return true;
        }
    }

    cout << "Неверный логин или пароль.\n";

    return false;
}

// ===============================
// Показ пользователей
// ===============================
void showUsers() {
    cout << "\n========== ПОЛЬЗОВАТЕЛИ ==========\n";

    bool found = false;

    for (const User& user : users) {
        if (user.login != currentUser) {
            cout << "- " << user.login << "\n";
            found = true;
        }
    }

    if (!found) {
        cout << "Других пользователей пока нет.\n";
    }
}

// ===============================
// Открытие чата
// ===============================
void openChat() {
    string otherUser;

    cout << "\nВведите логин собеседника: ";
    cin >> otherUser;

    if (!userExists(otherUser)) {
        cout << "Такого пользователя нет.\n";
        return;
    }

    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cout << "\n================================\n";
    cout << "          ЧАТ С " << otherUser << "\n";
    cout << "================================\n";

    bool hasMessages = false;

    for (const Message& message : messages) {
        bool firstDirection =
            message.from == currentUser &&
            message.to == otherUser;

        bool secondDirection =
            message.from == otherUser &&
            message.to == currentUser;

        if (firstDirection || secondDirection) {
            cout << message.from
                 << ": "
                 << message.text
                 << "\n";

            hasMessages = true;
        }
    }

    if (!hasMessages) {
        cout << "История сообщений пуста.\n";
    }

    cout << "\nВведите сообщение.\n";
    cout << "Для выхода напишите /exit\n\n";

    while (true) {
        cout << currentUser << ": ";

        string text;
        getline(cin, text);

        if (text == "/exit") {
            break;
        }

        if (text.empty()) {
            continue;
        }

        messages.push_back({
            currentUser,
            otherUser,
            text
        });

        saveMessages();
    }
}

// ===============================
// Меню мессенджера
// ===============================
void messengerMenu() {
    while (true) {
        cout << "\n";
        cout << "================================\n";
        cout << "        PIXEL MESSENGER\n";
        cout << "================================\n";
        cout << "Аккаунт: " << currentUser << "\n\n";

        cout << "1. Пользователи\n";
        cout << "2. Открыть чат\n";
        cout << "3. Выйти из аккаунта\n";
        cout << "0. Закрыть программу\n";

        cout << "\nВыберите пункт: ";

        int choice;

        if (!(cin >> choice)) {
            cin.clear();
            cin.ignore(
                numeric_limits<streamsize>::max(),
                '\n'
            );

            cout << "Введите число.\n";
            continue;
        }

        switch (choice) {
            case 1:
                showUsers();
                break;

            case 2:
                openChat();
                break;

            case 3:
                currentUser.clear();
                cout << "Вы вышли из аккаунта.\n";
                return;

            case 0:
                cout << "PIXEL MESSENGER закрыт.\n";
                exit(0);

            default:
                cout << "Такого пункта нет.\n";
        }
    }
}

// ===============================
// Главное меню
// ===============================
int main() {
    loadUsers();
    loadMessages();

    while (true) {
        cout << "\n";
        cout << "================================\n";
        cout << "        PIXEL MESSENGER\n";
        cout << "          C++ EDITION\n";
        cout << "================================\n";

        cout << "1. Регистрация\n";
        cout << "2. Вход\n";
        cout << "0. Выход\n";

        cout << "\nВыберите пункт: ";

        int choice;

        if (!(cin >> choice)) {
            cin.clear();
            cin.ignore(
                numeric_limits<streamsize>::max(),
                '\n'
            );

            cout << "Введите число.\n";
            continue;
        }

        switch (choice) {
            case 1:
                registerUser();
                break;

            case 2:
                if (loginUser()) {
                    messengerMenu();
                }
                break;

            case 0:
                cout << "Программа завершена.\n";
                return 0;

            default:
                cout << "Такого пункта нет.\n";
        }
    }
}