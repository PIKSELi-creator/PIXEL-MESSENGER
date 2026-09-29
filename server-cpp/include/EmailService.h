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
