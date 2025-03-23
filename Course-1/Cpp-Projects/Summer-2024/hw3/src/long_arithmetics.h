// Copyright 2024 <Dyshnui linter inc.>
#pragma once
#include <string>
#include <iostream>


class LongNumber {
    bool positive;
    int maxLength;
    int base;
    int* container;
    void resize(int);
    int getSize() const;
 public:
    LongNumber();
    explicit LongNumber(const std::string&);
    ~LongNumber();
    LongNumber(const LongNumber&);
    LongNumber(LongNumber&&);
    LongNumber& operator=(LongNumber&&);
    LongNumber operator=(const LongNumber&);
    LongNumber operator+(const LongNumber&) const;
    LongNumber operator-(const LongNumber&) const;
    LongNumber operator-() const;
    LongNumber operator*(const LongNumber&) const;
    bool operator<(const LongNumber&) const;
    bool operator==(const LongNumber&) const;
    friend std::ostream& operator<<(std::ostream&, const LongNumber&);
};
