#pragma once
#include <string>
#include <functional>


class TokenParser
{
    std::function<void(const std::string&)> stringCallback;
    std::function<void(const int&)> numberCallback;
    std::function<void()> beginCallback;
    std::function<void()> endCallback;
    void processToken(std::string&);
    bool isNumerical(const std::string&);
public:
    void parse(const std::string&);
    TokenParser() = delete;
    TokenParser(std::function<void(const std::string&)>, std::function<void(const int&)> numberCallback,
        std::function<void()> beginCallback, std::function<void()> endCallback);
};