from pathlib import Path
from urllib.request import urlopen

ROOT = Path(__file__).resolve().parent
SERVER = ROOT / "server-cpp"

FILES = {
    "CMakeLists.txt": r'''
cmake_minimum_required(VERSION 3.16)

project(pixel_chat_server)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_library(SQLITE3_LIB sqlite3 REQUIRED)
find_library(CURL_LIB curl REQUIRED)
find_library(CRYPTO_LIB crypto REQUIRED)

add_executable(pixel_chat_server
    src/main.cpp
    src/Database.cpp
    src/EmailService.cpp
)

target_include_directories(pixel_chat_server
    PRIVATE
    include
)

target_link_libraries(pixel_chat_server
    ${SQLITE3_LIB}
    ${CURL_LIB}
    ${CRYPTO_LIB}
    pthread
)
''',

    "include/Database.h": r'''
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
''',

    "src/Database.cpp": r'''
#include "Database.h"

#include <sqlite3.h>

#include <stdexcept>

namespace {
sqlite3* asDb(void* value) {
    return static_cast<sqlite3*>(value);
}
}

Database::Database(const std::string& path)
    : databasePath(path) {
    open();
}

Database::~Database() {
    close();
}

void Database::open() {
    sqlite3* connection = nullptr;

    if (sqlite3_open(databasePath.c_str(), &connection) != SQLITE_OK) {
        if (connection) {
            sqlite3_close(connection);
        }

        throw std::runtime_error("Could not open SQLite database");
    }

    db = connection;
}

void Database::close() {
    if (db) {
        sqlite3_close(asDb(db));
        db = nullptr;
    }
}

void Database::execute(const std::string& sql) {
    char* error = nullptr;

    const int result = sqlite3_exec(
        asDb(db),
        sql.c_str(),
        nullptr,
        nullptr,
        &error
    );

    if (result != SQLITE_OK) {
        std::string message =
            error ? error : "SQLite error";

        sqlite3_free(error);

        throw std::runtime_error(message);
    }
}

void Database::createTables() {
    execute(R"SQL(
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT UNIQUE NOT NULL,
            name TEXT NOT NULL,
            username TEXT UNIQUE NOT NULL,
            bio TEXT NOT NULL DEFAULT '',
            registered_at TEXT NOT NULL
        );

        CREATE TABLE IF NOT EXISTS verification_codes (
            email TEXT PRIMARY KEY,
            code_hash TEXT NOT NULL,
            expires_at INTEGER NOT NULL
        );

        CREATE TABLE IF NOT EXISTS verification_tokens (
            token TEXT PRIMARY KEY,
            email TEXT NOT NULL,
            expires_at INTEGER NOT NULL
        );

        CREATE TABLE IF NOT EXISTS sessions (
            token TEXT PRIMARY KEY,
            user_id INTEGER NOT NULL,
            expires_at INTEGER NOT NULL
        );
    )SQL");
}

bool Database::initialize() {
    if (!db) {
        return false;
    }

    createTables();
    return true;
}

bool Database::usernameExists(
    const std::string& username
) {
    const char* sql =
        "SELECT 1 FROM users WHERE username = ? LIMIT 1";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt,
        1,
        username.c_str(),
        -1,
        SQLITE_TRANSIENT
    );

    const bool exists =
        sqlite3_step(stmt) == SQLITE_ROW;

    sqlite3_finalize(stmt);

    return exists;
}

std::optional<User> Database::findUserByUsername(
    const std::string& username
) {
    const char* sql =
        "SELECT id, email, name, username, bio, registered_at "
        "FROM users WHERE lower(username) = lower(?) LIMIT 1";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt,
        1,
        username.c_str(),
        -1,
        SQLITE_TRANSIENT
    );

    if (sqlite3_step(stmt) != SQLITE_ROW) {
        sqlite3_finalize(stmt);
        return std::nullopt;
    }

    User user;

    user.id =
        sqlite3_column_int(stmt, 0);

    user.email = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 1)
    );

    user.name = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 2)
    );

    user.username = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 3)
    );

    user.bio = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 4)
    );

    user.registeredAt = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 5)
    );

    sqlite3_finalize(stmt);

    return user;
}

std::optional<User> Database::findUserByEmail(
    const std::string& email
) {
    const char* sql =
        "SELECT id, email, name, username, bio, registered_at "
        "FROM users WHERE lower(email) = lower(?) LIMIT 1";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt,
        1,
        email.c_str(),
        -1,
        SQLITE_TRANSIENT
    );

    if (sqlite3_step(stmt) != SQLITE_ROW) {
        sqlite3_finalize(stmt);
        return std::nullopt;
    }

    User user;

    user.id = sqlite3_column_int(stmt, 0);
    user.email = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 1)
    );
    user.name = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 2)
    );
    user.username = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 3)
    );
    user.bio = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 4)
    );
    user.registeredAt = reinterpret_cast<const char*>(
        sqlite3_column_text(stmt, 5)
    );

    sqlite3_finalize(stmt);

    return user;
}

int Database::createUser(
    const std::string& email,
    const std::string& name,
    const std::string& username,
    const std::string& bio,
    const std::string& registeredAt
) {
    const char* sql =
        "INSERT INTO users "
        "(email, name, username, bio, registered_at) "
        "VALUES (?, ?, ?, ?, ?)";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, email.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_text(
        stmt, 2, name.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_text(
        stmt, 3, username.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_text(
        stmt, 4, bio.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_text(
        stmt, 5, registeredAt.c_str(), -1, SQLITE_TRANSIENT
    );

    if (sqlite3_step(stmt) != SQLITE_DONE) {
        sqlite3_finalize(stmt);
        return -1;
    }

    sqlite3_finalize(stmt);

    return static_cast<int>(
        sqlite3_last_insert_rowid(asDb(db))
    );
}

bool Database::saveVerificationCode(
    const std::string& email,
    const std::string& codeHash,
    long long expiresAt
) {
    const char* sql =
        "INSERT INTO verification_codes "
        "(email, code_hash, expires_at) "
        "VALUES (?, ?, ?) "
        "ON CONFLICT(email) DO UPDATE SET "
        "code_hash = excluded.code_hash, "
        "expires_at = excluded.expires_at";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, email.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_text(
        stmt, 2, codeHash.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_int64(stmt, 3, expiresAt);

    const bool ok =
        sqlite3_step(stmt) == SQLITE_DONE;

    sqlite3_finalize(stmt);

    return ok;
}

std::optional<std::pair<std::string, long long>>
Database::getVerificationCode(
    const std::string& email
) {
    const char* sql =
        "SELECT code_hash, expires_at "
        "FROM verification_codes "
        "WHERE email = ? LIMIT 1";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, email.c_str(), -1, SQLITE_TRANSIENT
    );

    if (sqlite3_step(stmt) != SQLITE_ROW) {
        sqlite3_finalize(stmt);
        return std::nullopt;
    }

    std::string hash =
        reinterpret_cast<const char*>(
            sqlite3_column_text(stmt, 0)
        );

    long long expires =
        sqlite3_column_int64(stmt, 1);

    sqlite3_finalize(stmt);

    return std::make_pair(hash, expires);
}

void Database::deleteVerificationCode(
    const std::string& email
) {
    const char* sql =
        "DELETE FROM verification_codes WHERE email = ?";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, email.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_step(stmt);
    sqlite3_finalize(stmt);
}

bool Database::saveVerificationToken(
    const std::string& token,
    const std::string& email,
    long long expiresAt
) {
    const char* sql =
        "INSERT INTO verification_tokens "
        "(token, email, expires_at) "
        "VALUES (?, ?, ?)";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, token.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_text(
        stmt, 2, email.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_int64(stmt, 3, expiresAt);

    const bool ok =
        sqlite3_step(stmt) == SQLITE_DONE;

    sqlite3_finalize(stmt);

    return ok;
}

std::optional<std::pair<std::string, long long>>
Database::getVerificationToken(
    const std::string& token
) {
    const char* sql =
        "SELECT email, expires_at "
        "FROM verification_tokens "
        "WHERE token = ? LIMIT 1";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, token.c_str(), -1, SQLITE_TRANSIENT
    );

    if (sqlite3_step(stmt) != SQLITE_ROW) {
        sqlite3_finalize(stmt);
        return std::nullopt;
    }

    std::string email =
        reinterpret_cast<const char*>(
            sqlite3_column_text(stmt, 0)
        );

    long long expires =
        sqlite3_column_int64(stmt, 1);

    sqlite3_finalize(stmt);

    return std::make_pair(email, expires);
}

void Database::deleteVerificationToken(
    const std::string& token
) {
    const char* sql =
        "DELETE FROM verification_tokens WHERE token = ?";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, token.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_step(stmt);
    sqlite3_finalize(stmt);
}

bool Database::saveSession(
    const std::string& token,
    int userId,
    long long expiresAt
) {
    const char* sql =
        "INSERT INTO sessions "
        "(token, user_id, expires_at) "
        "VALUES (?, ?, ?)";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, token.c_str(), -1, SQLITE_TRANSIENT
    );

    sqlite3_bind_int(stmt, 2, userId);
    sqlite3_bind_int64(stmt, 3, expiresAt);

    const bool ok =
        sqlite3_step(stmt) == SQLITE_DONE;

    sqlite3_finalize(stmt);

    return ok;
}

std::optional<int> Database::getUserIdBySession(
    const std::string& token
) {
    const char* sql =
        "SELECT user_id FROM sessions "
        "WHERE token = ? AND expires_at > strftime('%s','now') "
        "LIMIT 1";

    sqlite3_stmt* stmt = nullptr;

    sqlite3_prepare_v2(
        asDb(db),
        sql,
        -1,
        &stmt,
        nullptr
    );

    sqlite3_bind_text(
        stmt, 1, token.c_str(), -1, SQLITE_TRANSIENT
    );

    if (sqlite3_step(stmt) != SQLITE_ROW) {
        sqlite3_finalize(stmt);
        return std::nullopt;
    }

    int userId = sqlite3_column_int(stmt, 0);

    sqlite3_finalize(stmt);

    return userId;
}
''',

    "include/EmailService.h": r'''
#pragma once

#include <string>

class EmailService {
public:
    EmailService(
        std::string smtpUser,
        std::string smtpPassword
    );

    bool sendVerificationCode(
        const std::string& recipient,
        const std::string& code
    );

private:
    std::string smtpUser;
    std::string smtpPassword;
};
''',

    "src/EmailService.cpp": r'''
#include "EmailService.h"

#include <curl/curl.h>

#include <string>

namespace {

struct MailPayload {
    std::string data;
    std::size_t position = 0;
};

size_t readCallback(
    char* buffer,
    size_t size,
    size_t nmemb,
    void* userdata
) {
    auto* payload =
        static_cast<MailPayload*>(userdata);

    const std::size_t capacity = size * nmemb;

    const std::size_t remaining =
        payload->data.size() - payload->position;

    const std::size_t amount =
        remaining < capacity ? remaining : capacity;

    if (amount > 0) {
        std::copy_n(
            payload->data.data() + payload->position,
            amount,
            buffer
        );

        payload->position += amount;
    }

    return amount;
}

}

EmailService::EmailService(
    std::string user,
    std::string password
)
    : smtpUser(std::move(user)),
      smtpPassword(std::move(password)) {
}

bool EmailService::sendVerificationCode(
    const std::string& recipient,
    const std::string& code
) {
    if (smtpUser.empty() || smtpPassword.empty()) {
        return false;
    }

    CURL* curl = curl_easy_init();

    if (!curl) {
        return false;
    }

    MailPayload payload;

    payload.data =
        "To: " + recipient + "\r\n"
        "From: " + smtpUser + "\r\n"
        "Subject: PIXEL CHAT verification code\r\n"
        "Content-Type: text/plain; charset=UTF-8\r\n"
        "\r\n"
        "Your PIXEL CHAT verification code is: " +
        code +
        "\r\n";

    struct curl_slist* recipients = nullptr;

    recipients = curl_slist_append(
        recipients,
        recipient.c_str()
    );

    curl_easy_setopt(
        curl,
        CURLOPT_URL,
        "smtp://smtp.gmail.com:587"
    );

    curl_easy_setopt(
        curl,
        CURLOPT_USE_SSL,
        CURLUSESSL_ALL
    );

    curl_easy_setopt(
        curl,
        CURLOPT_USERNAME,
        smtpUser.c_str()
    );

    curl_easy_setopt(
        curl,
        CURLOPT_PASSWORD,
        smtpPassword.c_str()
    );

    curl_easy_setopt(
        curl,
        CURLOPT_MAIL_FROM,
        smtpUser.c_str()
    );

    curl_easy_setopt(
        curl,
        CURLOPT_MAIL_RCPT,
        recipients
    );

    curl_easy_setopt(
        curl,
        CURLOPT_READFUNCTION,
        readCallback
    );

    curl_easy_setopt(
        curl,
        CURLOPT_READDATA,
        &payload
    );

    curl_easy_setopt(
        curl,
        CURLOPT_UPLOAD,
        1L
    );

    CURLcode result =
        curl_easy_perform(curl);

    curl_slist_free_all(recipients);
    curl_easy_cleanup(curl);

    return result == CURLE_OK;
}
''',

    "src/main.cpp": r'''
#include "Database.h"
#include "EmailService.h"
#include "httplib.h"
#include "json.hpp"

#include <openssl/sha.h>
#include <openssl/rand.h>

#include <chrono>
#include <cstdlib>
#include <iomanip>
#include <iostream>
#include <random>
#include <sstream>
#include <string>

using json = nlohmann::json;

namespace {

std::string env(
    const char* name,
    const std::string& fallback = ""
) {
    const char* value = std::getenv(name);

    if (!value) {
        return fallback;
    }

    return value;
}

long long nowSeconds() {
    return std::chrono::duration_cast<
        std::chrono::seconds
    >(
        std::chrono::system_clock::now().time_since_epoch()
    ).count();
}

std::string nowIso() {
    return std::to_string(nowSeconds());
}

std::string sha256(
    const std::string& value
) {
    unsigned char hash[SHA256_DIGEST_LENGTH];

    SHA256(
        reinterpret_cast<const unsigned char*>(
            value.data()
        ),
        value.size(),
        hash
    );

    std::ostringstream output;

    for (unsigned char byte : hash) {
        output
            << std::hex
            << std::setw(2)
            << std::setfill('0')
            << static_cast<int>(byte);
    }

    return output.str();
}

std::string randomHex(
    std::size_t bytes
) {
    std::string result(bytes * 2, '0');

    std::vector<unsigned char> buffer(bytes);

    if (RAND_bytes(
        buffer.data(),
        static_cast<int>(buffer.size())
    ) != 1) {
        throw std::runtime_error(
            "RAND_bytes failed"
        );
    }

    static const char* hex =
        "0123456789abcdef";

    for (std::size_t i = 0; i < bytes; ++i) {
        result[i * 2] =
            hex[(buffer[i] >> 4) & 0xF];

        result[i * 2 + 1] =
            hex[buffer[i] & 0xF];
    }

    return result;
}

std::string generateCode() {
    std::random_device random;

    std::mt19937 generator(random());

    std::uniform_int_distribution<int> distribution(
        100000,
        999999
    );

    return std::to_string(
        distribution(generator)
    );
}

std::string normalizeUsername(
    std::string username
) {
    if (!username.empty() &&
        username.front() == '@') {
        username.erase(username.begin());
    }

    for (char& character : username) {
        if (character == ' ') {
            character = '_';
        }
    }

    return username;
}

std::string getBearerToken(
    const httplib::Request& request
) {
    if (!request.has_header("Authorization")) {
        return "";
    }

    const std::string header =
        request.get_header_value("Authorization");

    const std::string prefix =
        "Bearer ";

    if (header.rfind(prefix, 0) != 0) {
        return "";
    }

    return header.substr(prefix.size());
}

void jsonResponse(
    httplib::Response& response,
    const json& body,
    int status = 200
) {
    response.status = status;

    response.set_content(
        body.dump(),
        "application/json"
    );
}

bool validEmail(
    const std::string& email
) {
    const auto at = email.find('@');

    const auto dot = email.find(
        '.',
        at == std::string::npos ? 0 : at
    );

    return at != std::string::npos &&
           dot != std::string::npos &&
           at > 0 &&
           dot > at + 1 &&
           dot + 1 < email.size();
}

}

int main() {
    std::cout
        << "Starting PIXEL CHAT C++ server...\n";

    Database database(
        env(
            "PIXEL_CHAT_DB",
            "data/pixel_chat.db"
        )
    );

    if (!database.initialize()) {
        std::cerr
            << "Database initialization failed.\n";

        return 1;
    }

    curl_global_init(CURL_GLOBAL_DEFAULT);

    EmailService emailService(
        env("SMTP_USER"),
        env("SMTP_APP_PASSWORD")
    );

    httplib::Server server;

    server.set_default_headers({
        {"Access-Control-Allow-Origin", "*"},
        {"Access-Control-Allow-Headers",
         "Content-Type, Authorization"},
        {"Access-Control-Allow-Methods",
         "GET, POST, OPTIONS"}
    });

    server.Options(
        R"(.*)",
        [](const httplib::Request&,
           httplib::Response& response) {
            response.status = 204;
        }
    );

    server.Get(
        "/health",
        [](const httplib::Request&,
           httplib::Response& response) {
            jsonResponse(
                response,
                {
                    {"ok", true},
                    {"service", "PIXEL CHAT"},
                    {"server", "C++"}
                }
            );
        }
    );

    server.Post(
        "/auth/request-code",
        [&](const httplib::Request& request,
            httplib::Response& response) {

            try {
                const json body =
                    json::parse(request.body);

                const std::string email =
                    body.value("email", "");

                if (!validEmail(email)) {
                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error", "invalid_email"}
                        },
                        400
                    );

                    return;
                }

                const std::string code =
                    generateCode();

                const std::string hash =
                    sha256(code);

                const long long expires =
                    nowSeconds() + 600;

                database.saveVerificationCode(
                    email,
                    hash,
                    expires
                );

                const bool sent =
                    emailService.sendVerificationCode(
                        email,
                        code
                    );

                std::cout
                    << "[AUTH] verification code for "
                    << email
                    << ": "
                    << code
                    << "\n";

                jsonResponse(
                    response,
                    {
                        {"ok", true},
                        {"email_sent", sent},
                        {"expires_in", 600}
                    }
                );

            } catch (...) {
                jsonResponse(
                    response,
                    {
                        {"ok", false},
                        {"error", "invalid_request"}
                    },
                    400
                );
            }
        }
    );

    server.Post(
        "/auth/verify-code",
        [&](const httplib::Request& request,
            httplib::Response& response) {

            try {
                const json body =
                    json::parse(request.body);

                const std::string email =
                    body.value("email", "");

                const std::string code =
                    body.value("code", "");

                auto saved =
                    database.getVerificationCode(email);

                if (!saved.has_value()) {
                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error", "code_not_found"}
                        },
                        400
                    );

                    return;
                }

                const auto& savedHash =
                    saved->first;

                const long long expires =
                    saved->second;

                if (nowSeconds() > expires) {
                    database.deleteVerificationCode(email);

                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error", "code_expired"}
                        },
                        400
                    );

                    return;
                }

                if (sha256(code) != savedHash) {
                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error", "invalid_code"}
                        },
                        400
                    );

                    return;
                }

                database.deleteVerificationCode(email);

                auto existing =
                    database.findUserByEmail(email);

                if (existing.has_value()) {
                    const std::string sessionToken =
                        randomHex(32);

                    database.saveSession(
                        sessionToken,
                        existing->id,
                        nowSeconds() + 2592000
                    );

                    jsonResponse(
                        response,
                        {
                            {"ok", true},
                            {"new_user", false},
                            {"session_token",
                             sessionToken}
                        }
                    );

                    return;
                }

                const std::string verificationToken =
                    randomHex(32);

                database.saveVerificationToken(
                    verificationToken,
                    email,
                    nowSeconds() + 900
                );

                jsonResponse(
                    response,
                    {
                        {"ok", true},
                        {"new_user", true},
                        {"verification_token",
                         verificationToken}
                    }
                );

            } catch (...) {
                jsonResponse(
                    response,
                    {
                        {"ok", false},
                        {"error", "invalid_request"}
                    },
                    400
                );
            }
        }
    );

    server.Post(
        "/auth/create-profile",
        [&](const httplib::Request& request,
            httplib::Response& response) {

            try {
                const json body =
                    json::parse(request.body);

                const std::string token =
                    body.value(
                        "verification_token",
                        ""
                    );

                const std::string name =
                    body.value("name", "");

                const std::string username =
                    normalizeUsername(
                        body.value("username", "")
                    );

                const std::string bio =
                    body.value("bio", "");

                if (token.empty() ||
                    name.empty() ||
                    username.empty()) {

                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error", "missing_fields"}
                        },
                        400
                    );

                    return;
                }

                if (username.size() < 3 ||
                    username.size() > 32) {

                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error",
                             "invalid_username_length"}
                        },
                        400
                    );

                    return;
                }

                for (char character : username) {
                    if (!std::isalnum(
                            static_cast<unsigned char>(
                                character
                            )
                        ) &&
                        character != '_') {

                        jsonResponse(
                            response,
                            {
                                {"ok", false},
                                {"error",
                                 "invalid_username"}
                            },
                            400
                        );

                        return;
                    }
                }

                auto savedToken =
                    database.getVerificationToken(
                        token
                    );

                if (!savedToken.has_value()) {
                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error",
                             "invalid_verification_token"}
                        },
                        400
                    );

                    return;
                }

                if (nowSeconds() >
                    savedToken->second) {

                    database.deleteVerificationToken(
                        token
                    );

                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error",
                             "verification_token_expired"}
                        },
                        400
                    );

                    return;
                }

                if (database.usernameExists(
                        username)) {

                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error",
                             "username_taken"}
                        },
                        409
                    );

                    return;
                }

                const std::string email =
                    savedToken->first;

                const int userId =
                    database.createUser(
                        email,
                        name,
                        username,
                        bio,
                        nowIso()
                    );

                if (userId <= 0) {
                    jsonResponse(
                        response,
                        {
                            {"ok", false},
                            {"error",
                             "user_creation_failed"}
                        },
                        500
                    );

                    return;
                }

                database.deleteVerificationToken(
                    token
                );

                const std::string sessionToken =
                    randomHex(32);

                database.saveSession(
                    sessionToken,
                    userId,
                    nowSeconds() + 2592000
                );

                jsonResponse(
                    response,
                    {
                        {"ok", true},
                        {"user_id", userId},
                        {"username", username},
                        {"session_token",
                         sessionToken}
                    }
                );

            } catch (...) {
                jsonResponse(
                    response,
                    {
                        {"ok", false},
                        {"error", "invalid_request"}
                    },
                    400
                );
            }
        }
    );

    server.Get(
        R"(/users/(.+))",
        [&](const httplib::Request& request,
            httplib::Response& response) {

            const std::string username =
                normalizeUsername(
                    request.matches[1]
                );

            auto user =
                database.findUserByUsername(
                    username
                );

            if (!user.has_value()) {
                jsonResponse(
                    response,
                    {
                        {"found", false}
                    },
                    404
                );

                return;
            }

            jsonResponse(
                response,
                {
                    {"found", true},
                    {"id", user->id},
                    {"username",
                     user->username},
                    {"name", user->name},
                    {"bio", user->bio},
                    {"registered_at",
                     user->registeredAt}
                }
            );
        }
    );

    server.Get(
        "/me",
        [&](const httplib::Request& request,
            httplib::Response& response) {

            const std::string token =
                getBearerToken(request);

            if (token.empty()) {
                jsonResponse(
                    response,
                    {
                        {"ok", false},
                        {"error", "unauthorized"}
                    },
                    401
                );

                return;
            }

            auto userId =
                database.getUserIdBySession(
                    token
                );

            if (!userId.has_value()) {
                jsonResponse(
                    response,
                    {
                        {"ok", false},
                        {"error", "invalid_session"}
                    },
                    401
                );

                return;
            }

            jsonResponse(
                response,
                {
                    {"ok", true},
                    {"user_id", *userId}
                }
            );
        }
    );

    std::cout << "\n";
    std::cout
        << "PIXEL CHAT C++ SERVER\n";
    std::cout
        << "HTTP: http://0.0.0.0:8080\n";
    std::cout
        << "Health: /health\n";
    std::cout
        << "Search: /users/<username>\n";
    std::cout
        << "Auth: /auth/request-code\n";
    std::cout << "\n";

    const bool running =
        server.listen(
            "0.0.0.0",
            8080
        );

    curl_global_cleanup();

    return running ? 0 : 1;
}
''',

    ".env.example": r'''
# Gmail SMTP
SMTP_USER=your_gmail@gmail.com
SMTP_APP_PASSWORD=your_16_character_app_password

# Optional database path
PIXEL_CHAT_DB=data/pixel_chat.db
''',
}


def write_file(relative_path: str, content: str):
    path = SERVER / relative_path
    path.parent.mkdir(parents=True, exist_ok=True)

    if path.exists():
        backup = path.with_suffix(
            path.suffix + ".backup_generator"
        )

        if not backup.exists():
            backup.write_bytes(path.read_bytes())

    path.write_text(content.lstrip("\n"), encoding="utf-8")


def download_file(url: str, destination: Path):
    destination.parent.mkdir(parents=True, exist_ok=True)

    if destination.exists() and destination.stat().st_size > 1000:
        print(f"[OK] {destination}")
        return

    print(f"[DOWNLOAD] {url}")

    with urlopen(url, timeout=30) as response:
        data = response.read()

    destination.write_bytes(data)

    print(f"[OK] downloaded {len(data)} bytes")


def main():
    print("=" * 50)
    print("PIXEL CHAT — BUILD GENERATOR")
    print("=" * 50)

    SERVER.mkdir(parents=True, exist_ok=True)

    for relative_path, content in FILES.items():
        write_file(relative_path, content)
        print(f"[WRITE] server-cpp/{relative_path}")

    download_file(
        "https://raw.githubusercontent.com/"
        "yhirose/cpp-httplib/master/httplib.h",
        SERVER / "include/httplib.h"
    )

    download_file(
        "https://raw.githubusercontent.com/"
        "nlohmann/json/develop/single_include/"
        "nlohmann/json.hpp",
        SERVER / "include/json.hpp"
    )

    (SERVER / "data").mkdir(
        parents=True,
        exist_ok=True
    )

    print("\nDone.")
    print("\nNext:")
    print("  cd ~/PIXEL-MESSENGER/server-cpp")
    print("  cmake -S . -B build")
    print("  cmake --build build")


if __name__ == "__main__":
    main()
