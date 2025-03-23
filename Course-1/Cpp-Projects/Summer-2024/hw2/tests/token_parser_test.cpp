#include <gtest/gtest.h>
#include "token_parser.h"


int numberCalls;
int stringCalls;


void startParsing() 
{
    std::cout << "Staring parsing:\n";
}


void endParsing() 
{
    std::cout << "Ending parsing\n";
}


void tokenNumerical(const std::string& token) 
{
    std::cout << "Numerical token found: " << token << "\n";
    numberCalls++;
}


void tokenString(const std::string& token) 
{
    std::cout << "String token found: " << token << "\n";
    stringCalls++;
}


TEST(TokenParisng, Test1)
{
    numberCalls = 0;
    stringCalls = 0;
    std::string input = "Two plus\ttwo\nis four";
    std::function<void (const std::string&)> stringCallback = tokenString;
    std::function<void (const std::string& str)> numberCallback = tokenNumerical;
    std::function<void ()> beginCallback = startParsing;
    std::function<void ()> endCallback = endParsing;
    TokenParser parser(stringCallback, numberCallback, beginCallback, endCallback);
    parser.parse(input);
    EXPECT_EQ(numberCalls, 0);
    EXPECT_EQ(stringCalls, 5);
}


TEST(TokenParisng, Test2)
{
    numberCalls = 0;
    stringCalls = 0;
    std::string input = "2 plus\t2\nis 4";
    std::function<void (const std::string&)> stringCallback = tokenString;
    std::function<void (const std::string& str)> numberCallback = tokenNumerical;
    std::function<void ()> beginCallback = startParsing;
    std::function<void ()> endCallback = endParsing;
    TokenParser parser(stringCallback, numberCallback, beginCallback, endCallback);
    parser.parse(input);
    EXPECT_EQ(numberCalls, 3);
    EXPECT_EQ(stringCalls, 2);
}


TEST(TokenParisng, Test3)
{
    numberCalls = 0;
    stringCalls = 0;
    std::string input = "\t2       plus     \t     two     \n    is  4  \n";
    std::function<void (const std::string&)> stringCallback = tokenString;
    std::function<void (const std::string& str)> numberCallback = tokenNumerical;
    std::function<void ()> beginCallback = startParsing;
    std::function<void ()> endCallback = endParsing;
    TokenParser parser(stringCallback, numberCallback, beginCallback, endCallback);
    parser.parse(input);
    EXPECT_EQ(numberCalls, 2);
    EXPECT_EQ(stringCalls, 3);
}