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
