// Copyright 2024 <Dyshnui linter inc.>
#include <iostream>
#include <algorithm>
#include <cmath>
#include <cstdint>
#include "src/long_arithmetics.h"


LongNumber::LongNumber() : positive{ true }, maxLength{ 1 },
                        base{ static_cast <int>(1e9) },
                        container{ new int[maxLength]() } {}


LongNumber::LongNumber(const std::string& number) :
                        base{ static_cast<int>(1e9) }
{
    positive = true;
    int i = 0;
    int dec = 0;
    if (number[0] == '-') {
        positive = false;
        dec++;
    }
    maxLength = number.length() / 9 + 1;
    container = new int[maxLength]();
    int writeIdx = 0;
    while (i < number.length() - dec) {
        int digit = number[number.length() - i - 1] - '0';
        int power = std::pow(10, i % 9);
        container[writeIdx] += digit * power;
        i++;
        if (i % 9 == 0) {
            writeIdx++;
        }
    }
}


LongNumber::~LongNumber() {
    delete[] container;
}


LongNumber::LongNumber(const LongNumber& longNumber) :
            positive{ longNumber.positive },
            maxLength{ longNumber.maxLength },
            base{ longNumber.base }, container{ new int[maxLength]() }
{
    for (int i = 0; i < maxLength; i++) {
        container[i] = longNumber.container[i];
    }
}


LongNumber::LongNumber(LongNumber&& longNumber) :
            positive{ longNumber.positive },
            maxLength{ longNumber.maxLength },
            base{ longNumber.base }
{
    container = longNumber.container;
    longNumber.container = nullptr;
    longNumber.maxLength = 0;
}



LongNumber LongNumber::operator=(const LongNumber& longNumber) {
    positive = longNumber.positive;
    maxLength = longNumber.maxLength;
    base = longNumber.base;
    delete[] container;
    container = new int[maxLength]();
    for (int i = 0; i < maxLength; i++) {
        container[i] = longNumber.container[i];
    }
    return *this;
}

LongNumber& LongNumber::operator=(LongNumber&& longNumber) {
    positive = longNumber.positive;
    maxLength = longNumber.maxLength;
    base = longNumber.base;
    delete[] container;
    container = longNumber.container;
    longNumber.container = nullptr;
    longNumber.maxLength = 0;
    return *this;
}


void LongNumber::resize(int size) {
    if (size <= maxLength) {
        return;
    }
    int* newContainer = new int[size]();
    for (int i = 0; i < maxLength; i++) {
        newContainer[i] = container[i];
    }
    delete[] container;
    maxLength = size;
    container = newContainer;
}


LongNumber LongNumber::operator+(const LongNumber& longNumber) const {
    if (!positive) {
        if (!longNumber.positive) {
            return -((-*this) + (-longNumber));
        } else {
            return longNumber - (-*this);
        }
    } else if (!longNumber.positive) {
        return *this - (-longNumber);
    }
    LongNumber res;
    res.resize(std::max(maxLength, longNumber.maxLength));
    for (int i = 0; i < std::min(maxLength, longNumber.maxLength); i++) {
        if ((i == res.maxLength - 1) &&
        (container[i] + longNumber.container[i] > base)) {
            res.resize(res.maxLength + 1);
        }
        res.container[i] += container[i] + longNumber.container[i];
        res.container[i + 1] += res.container[i] / base;
        res.container[i] = res.container[i] % base;
    }
    return res;
}


LongNumber LongNumber::operator-() const {
    LongNumber res(*this);
    res.positive = !res.positive;
    return res;
}


LongNumber LongNumber::operator-(const LongNumber& longNumber) const {
    if (!positive) {
        if (!longNumber.positive) {
            return (-longNumber) - (-*this);
        } else {
            return *this + (-longNumber);
        }
    } else if (!longNumber.positive) {
        return *this + (-longNumber);
    }
    if (*this < longNumber) {
        return -(longNumber - *this);
    }
    LongNumber res(*this);
    for (int i = 0; i < longNumber.maxLength; i++) {
        res.container[i] = res.container[i] - longNumber.container[i];
        if (res.container[i] < 0) {
            res.container[i] += base;
            res.container[i + 1] -= 1;
        }
    }
    return res;
}


LongNumber LongNumber::operator*(const LongNumber& longNumber) const {
    LongNumber res;
    res.resize(maxLength + longNumber.maxLength + 1);
    for (int i = 0; i < maxLength; i++) {
        int64_t tmp = 0;
        for (int j = 0; j < longNumber.maxLength; j++) {
            tmp += 1ll * container[i] * longNumber.container[j] +
            res.container[i + j];
            res.container[i + j] = tmp % base;
            tmp = tmp / base;
        }
    }
    if ((!positive && longNumber.positive) ||
    (positive && !longNumber.positive)) {
        return -res;
    }
    return res;
}


int LongNumber::getSize() const {
    int idx = 0;
    for (int i = 0; i < maxLength; i++) {
        if (container[i] > 0) {
            idx = i;
        }
    }
    return idx + 1;
}


bool LongNumber::operator<(const LongNumber& longNumber) const {
    if (getSize() < longNumber.getSize()) {
        return true;
    } else if (getSize() > longNumber.getSize()) {
        return false;
    }
    for (int i = getSize() - 1; i >= 0; i--) {
        if (container[i] < longNumber.container[i]) {
            return true;
        } else if (container[i] > longNumber.container[i]) {
            return false;
        }
    }
    return false;
}


bool LongNumber::operator==(const LongNumber& longNumber) const {
    if (positive != longNumber.positive) {
        return false;
    }
    if (getSize() != longNumber.getSize()) {
        return false;
    }
    for (int i = 0; i < getSize(); i++) {
        if (container[i] != longNumber.container[i]) {
            return false;
        }
    }
    return true;
}


std::ostream& operator<<(std::ostream& os, const LongNumber& longNumber) {
    if (!longNumber.positive) {
        os << '-';
    }
    for (int i = longNumber.getSize() - 1; i >= 0; i--) {
        std::string output = std::to_string(longNumber.container[i]);
        if (i != longNumber.getSize() - 1) {
            int dif = 9 - output.length();
            for (int j = 0; j < dif; j++) {
                output = '0' + output;
            }
        }
        os << output;
    }
    return os;
}
