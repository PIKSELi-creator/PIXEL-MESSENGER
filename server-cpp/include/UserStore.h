#pragma once

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
