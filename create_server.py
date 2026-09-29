from pathlib import Path

root = Path("server-cpp")

files = {
    "CMakeLists.txt": r'''cmake_minimum_required(VERSION 3.16)

project(pixel_chat_server)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable(pixel_chat_server
    src/main.cpp
    src/UserStore.cpp
)

target_include_directories(pixel_chat_server
    PRIVATE
    include
)
''',

    "include/User.h": r'''#pragma once

#include <string>

struct User {
    int id;
    std::string username;
    std::string name;
};
''',

    "include/UserStore.h": r'''#pragma once

#include "User.h"

#include <optional>
#include <string>
#include <vector>

class UserStore {
public:
    UserStore();

    void addUser(const User& user);

    std::optional<User> findByUsername(
        const std::string& username
    ) const;

private:
    std::vector<User> users;
};
''',

    "src/UserStore.cpp": r'''#include "UserStore.h"

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
''',

    "src/main.cpp": r'''#include "UserStore.h"

#include <iostream>
#include <string>

int main() {
    UserStore store;

    std::cout << "PIXEL CHAT C++ SERVER\n";
    std::cout << "Type username to search.\n";
    std::cout << "Type exit to stop.\n\n";

    while (true) {
        std::cout << "@";

        std::string username;
        std::cin >> username;

        if (username == "exit") {
            break;
        }

        auto user = store.findByUsername(username);

        if (user.has_value()) {
            std::cout << "\nUser found!\n";
            std::cout << "ID: " << user->id << "\n";
            std::cout << "Username: @" << user->username << "\n";
            std::cout << "Name: " << user->name << "\n\n";
        } else {
            std::cout << "\nUser not found.\n\n";
        }
    }

    return 0;
}
''',

    "data/users.json": r'''[
  {
    "id": 1,
    "username": "alex",
    "name": "Alex"
  },
  {
    "id": 2,
    "username": "pixeluser",
    "name": "Pixel User"
  }
]
'''
}

for relative_path, content in files.items():
    path = root / relative_path
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")

print("PIXEL CHAT C++ server created!")
print()

for path in sorted(root.rglob("*")):
    if path.is_file():
        print(path)
