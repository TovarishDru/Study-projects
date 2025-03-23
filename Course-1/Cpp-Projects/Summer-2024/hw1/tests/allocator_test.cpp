#include <gtest/gtest.h>
#include "allocator.h"


TEST(Allocation, StandardCase)
{
  Allocator allocator;
  allocator.makeAllocator(10);
  char* str = allocator.alloc(1);
  *str = 'a';
  EXPECT_EQ(*str, 'a');
}


TEST(Allocation, OverflowCase)
{
    Allocator allocator;
    allocator.makeAllocator(1);
    allocator.alloc(1);
    char* str = allocator.alloc(1);
    EXPECT_EQ(str, nullptr);
}


TEST(Allocation, ZeroAllocationCase)
{
    Allocator allocator;
    allocator.makeAllocator(1);
    char* str = allocator.alloc(0);
    EXPECT_EQ(str, nullptr);
}


TEST(Allocation, NeagtiveAllocationCase)
{
    Allocator allocator;
    allocator.makeAllocator(1);
    char* str = allocator.alloc(-1);
    EXPECT_EQ(str, nullptr);
}


TEST(Allocation, ResetAllocationCase)
{
    Allocator allocator;
    allocator.makeAllocator(1);
    allocator.alloc(1);
    allocator.reset();
    char* str = allocator.alloc(1);
    *str = 'a';
    EXPECT_EQ(*str, 'a');
}


TEST(Allocation, IntersectionAllocationCheck) {
    Allocator allocator;
    allocator.makeAllocator(10);
    char* str1 = allocator.alloc(5);
    char* str2 = allocator.alloc(5);
    for (int i = 0; i < 5; i++) {
        str1[i] = 'a';
    }
    for (int i = 0; i < 5; i++) {
        str2[i] = 'b';
    }
    for (int i = 0; i < 5; i++) {
        EXPECT_EQ(str1[i], 'a');
        EXPECT_EQ(str2[i], 'b');
    }
}