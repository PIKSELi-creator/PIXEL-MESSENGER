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
