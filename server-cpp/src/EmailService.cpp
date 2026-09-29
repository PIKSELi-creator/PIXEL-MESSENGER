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
