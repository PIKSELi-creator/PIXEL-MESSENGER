#include "UserStore.h"

#include <algorithm>
#include <cctype>

namespace {
std::string normalizeUsername(std::string username) {
    if (!username.empty() && username.front() == '@') {
        username.erase(username.begin());
    }

    std::transform(
        username.begin(),
        username.end(),
        username.begin(),
        [](unsigned char c) {
            return static_cast<char>(std::tolower(c));
        }
    );

    return username;
}
}

UserStore::UserStore() {
    addUser({
        1,
        "alex",
        "Alex"
    });

    addUser({
        2,
        "pixeluser",
        "Pixel User"
    });
}

void UserStore::addUser(const User& user) {
    users.push_back(user);
}

std::optional<User> UserStore::findByUsername(
    const std::string& username
) const {
    const std::string target = normalizeUsername(username);

    for (const auto& user : users) {
        if (normalizeUsername(user.username) == target) {
            return user;
        }
    }

    return std::nullopt;
}
