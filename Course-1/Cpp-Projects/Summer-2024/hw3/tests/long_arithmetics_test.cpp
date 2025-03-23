// Copyright 2024 <Dyshnui linter inc.>
#include <gtest/gtest.h>
#include "src/long_arithmetics.h"


TEST(SummationTest, Test1) {
    LongNumber number1("1000000000");
    LongNumber number2("2000000000");
    LongNumber res = number1 + number2;
    LongNumber exp("3000000000");
    std::cout << res << "\n";
    EXPECT_EQ(res, exp);
}


TEST(SummationTest, Test2) {
    LongNumber number1("1000000000");
    LongNumber number2("-2000000000");
    LongNumber exp("-1000000000");
    LongNumber res = number1 + number2;
    std::cout << res << "\n";
    EXPECT_EQ(res, exp);
}


TEST(SummationTest, Test3) {
    LongNumber number1("-1000000000");
    LongNumber number2("-2000000000");
    LongNumber exp("-3000000000");
    std::cout << number1 + number2 << "\n";
    EXPECT_EQ(number1 + number2, exp);
}


TEST(SummationTest, Test4) {
    LongNumber number1("-1000000000");
    LongNumber number2("2000000000");
    LongNumber exp("1000000000");
    std::cout << number1 + number2 << "\n";
    EXPECT_EQ(number1 + number2, exp);
}


TEST(SubtractionTest, Test1) {
    LongNumber number1("1000000000");
    LongNumber number2("2000000000");
    LongNumber exp("-1000000000");
    std::cout << number1 - number2 << "\n";
    EXPECT_EQ(number1 - number2, exp);
}


TEST(SubtractionTest, Test2) {
    LongNumber number1("1000000000");
    LongNumber number2("-2000000000");
    LongNumber exp("3000000000");
    std::cout << number1 - number2 << "\n";
    EXPECT_EQ(number1 - number2, exp);
}


TEST(SubtractionTest, Test3) {
    LongNumber number1("-1000000000");
    LongNumber number2("-2000000000");
    LongNumber exp("1000000000");
    std::cout << number1 - number2 << "\n";
    EXPECT_EQ(number1 - number2, exp);
}


TEST(SubtractionTest, Test4) {
    LongNumber number1("-1000000000");
    LongNumber number2("2000000000");
    LongNumber exp("-3000000000");
    std::cout << number1 - number2 << "\n";
    EXPECT_EQ(number1 - number2, exp);
}


TEST(MultiplicationTest, Test1) {
    LongNumber number1("1000000000");
    LongNumber number2("2000000000");
    LongNumber exp("2000000000000000000");
    std::cout << number1 * number2 << "\n";
    EXPECT_EQ(number1 * number2, exp);
}


TEST(MultiplicationTest, Test2) {
    LongNumber number1("1000000000");
    LongNumber number2("-2000000000");
    LongNumber exp("-2000000000000000000");
    std::cout << number1 * number2 << "\n";
    EXPECT_EQ(number1 * number2, exp);
}


TEST(MultiplicationTest, Test3) {
    LongNumber number1("-1000000000");
    LongNumber number2("-2000000000");
    LongNumber exp("2000000000000000000");
    std::cout << number1 * number2 << "\n";
    EXPECT_EQ(number1 * number2, exp);
}


TEST(MultiplicationTest, Test4) {
    LongNumber number1("-1000000000");
    LongNumber number2("2000000000");
    LongNumber exp("-2000000000000000000");
    std::cout << number1 * number2 << "\n";
    EXPECT_EQ(number1 * number2, exp);
}
