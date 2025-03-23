#include <sstream>
#include "token_parser.h"


void TokenParser::parse(const std::string& toParse)
{
    beginCallback();
    std::stringstream stream(toParse);
    std::string token = "";
    while (stream >> token)
    {
        processToken(token);
    }
    endCallback();
}


void TokenParser::processToken(std::string& token)
{
    if (token.length() > 0)
    {
        if (isNumerical(token))
        {
            numberCallback(stoi(token));
        }
        else
        {
            stringCallback(token);
        }
        token.clear();
    }
}

bool TokenParser::isNumerical(const std::string& token)
{
    for (int idx = 0; idx < token.length(); idx++) 
    {
        if (!(('0' <= token[idx]) && (token[idx] <= '9')))
        {
            return false;
        }
    }
    return true;
}


TokenParser::TokenParser(std::function<void(const std::string&)> stringCallback,
    std::function<void(const int&)> numberCallback,
    std::function<void()> beginCallback, std::function<void()> endCallback) : stringCallback(stringCallback),
    numberCallback(numberCallback), beginCallback(beginCallback), endCallback(endCallback) {};