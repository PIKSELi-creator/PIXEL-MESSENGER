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


    return running ? 0 : 1;
}
