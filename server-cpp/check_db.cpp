#include <sqlite3.h>
#include <iostream>

int main() {
    sqlite3* db = nullptr;

    if (sqlite3_open("data/pixel_chat.db", &db) != SQLITE_OK) {
        std::cerr << "DB OPEN ERROR\n";
        return 1;
    }

    const char* sql =
        "SELECT token, email, expires_at "
        "FROM verification_tokens;";

    sqlite3_stmt* stmt = nullptr;

    if (sqlite3_prepare_v2(db, sql, -1, &stmt, nullptr) != SQLITE_OK) {
        std::cerr << "QUERY ERROR: "
                  << sqlite3_errmsg(db) << "\n";
        sqlite3_close(db);
        return 1;
    }

    bool found = false;

    while (sqlite3_step(stmt) == SQLITE_ROW) {
        found = true;

        const char* token =
            reinterpret_cast<const char*>(
                sqlite3_column_text(stmt, 0)
            );

        const char* email =
            reinterpret_cast<const char*>(
                sqlite3_column_text(stmt, 1)
            );

        long long expires =
            sqlite3_column_int64(stmt, 2);

        std::cout << "TOKEN: " << (token ? token : "") << "\n";
        std::cout << "EMAIL: " << (email ? email : "") << "\n";
        std::cout << "EXPIRES: " << expires << "\n";
        std::cout << "----------------\n";
    }

    if (!found) {
        std::cout << "NO VERIFICATION TOKENS\n";
    }

    sqlite3_finalize(stmt);
    sqlite3_close(db);
    return 0;
}
