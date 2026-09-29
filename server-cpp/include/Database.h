#pragma once

#include <optional>
#include <string>

struct User {
    int id = 0;
    std::string email;
    std::string name;
    std::string username;
    std::string bio;
    std::string registeredAt;
};

class Database {
public:
    explicit Database(const std::string& path);
    ~Database();

    bool initialize();

    bool usernameExists(const std::string& username);

    std::optional<User> findUserByUsername(
        const std::string& username
    );

    std::optional<User> findUserByEmail(
        const std::string& email
    );

    int createUser(
        const std::string& email,
        const std::string& name,
        const std::string& username,
        const std::string& bio,
        const std::string& registeredAt
    );

    bool saveVerificationCode(
        const std::string& email,
        const std::string& codeHash,
        long long expiresAt
    );

    std::optional<std::pair<std::string, long long>>
    getVerificationCode(const std::string& email);

    void deleteVerificationCode(const std::string& email);

    bool saveVerificationToken(
        const std::string& token,
        const std::string& email,
        long long expiresAt
    );

    std::optional<std::pair<std::string, long long>>
    getVerificationToken(const std::string& token);

    void deleteVerificationToken(const std::string& token);

    bool saveSession(
        const std::string& token,
        int userId,
        long long expiresAt
    );

    std::optional<int> getUserIdBySession(
        const std::string& token
    );

private:
    void execute(const std::string& sql);

    void open();

    void close();

    void createTables();

    void* db = nullptr;
    std::string databasePath;
};
