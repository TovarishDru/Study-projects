#include <iostream>
#include <stdarg.h>
#include <sstream>
#include <vector>


class Token
{
    public:
        int type;
        std::string word;
        Token(int type, const std::string& word) : type(type), word(word) {}
};


class TokenException : public std::exception
{
        std::string message;
    public:
        TokenException(const std::string& message) : message{message} {}
        virtual const char* what() const noexcept override
        {
            return message.c_str();
        }
}; 


class InvalidBracketsException : public std::exception
{
        std::string message;
    public:
        InvalidBracketsException(const std::string& message) : message{message} {}
        virtual const char* what() const noexcept override
        {
            return message.c_str();
        }
};


std::stringstream getWord(char* ptr)
{
    int j = 0;
    std::stringstream stream;
    while (ptr[j] != '\0')
    {
        stream << ptr[j];
        j++;
    }
    return stream;
}


std::stringstream getId(const std::string& input, int& i)
{
    i++;
    std::stringstream stream;
    while (input[i] != '}' && i < input.length())
    {
        if (input[i] < '0' || '9' < input[i])
        {
            throw TokenException("Non-numerical token was provided");
        }
        stream << input[i];
        i++;
    }
    if (i == input.length())
    {
        throw InvalidBracketsException("Invalid brackets sequence was provided");
    }
    return stream;
}


std::vector<Token> parse(const std::string& input, int numberOfTokens)
{
    std::vector<Token> buf;
    std::stringstream stream;
    for (int i = 0; i < input.length(); i++)
    {
        if (input[i] == '{')
        {
            if (!stream.str().empty())
            {

                buf.push_back({ 0, stream.str() });
                stream.str(std::string());
            }
            std::stringstream tmp(getId(input, i));
            if (stoi(tmp.str()) >= numberOfTokens)
            {
                throw TokenException("Invalid nummber of tokens");
            }
            buf.push_back({ 1, tmp.str() });
            continue;
        }
        if (input[i] == '}')
        {
            throw InvalidBracketsException("Invalid brackets sequence was provided");
        }
        stream << input[i];
    }
    if (!stream.eof() && !stream.str().empty())
    {
        buf.push_back({ 0, stream.str() });
    }
    return buf;
}


template <typename T> concept Printable = requires(T obj)
{
    std::cout << obj;
};


template <Printable... Args>
std::string format(std::string input, Args... args)
{
    std::vector<std::string> tokens;
    for (auto i : { args... })
    {
        std::stringstream tmp;
        tmp << i;
        tokens.push_back(tmp.str());
    }
    int n = sizeof...(args);
    std::vector<Token> buf = parse(input, n);
    std::stringstream res;
    for (int i = 0; i < buf.size(); i++)
    {
        if (buf[i].type == 0)
        {
            res << buf[i].word;
        }
        else
        {
            int tokenIdx = stoi(buf[i].word);
            res << tokens[tokenIdx];
        }
    }
    return res.str();
}