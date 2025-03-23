#include <gtest/gtest.h>
#include "format.h"


TEST(FormatTest, Test1)
{
    EXPECT_EQ(format("{1}+{1} = {0}", 2, 1), "1+1 = 2");
};


TEST(FormatTest, Test2)
{
    EXPECT_THROW(format("{1}+{2} = {0}", "2", "one"), TokenException);
};


TEST(FormatTest, Test3)
{
    EXPECT_THROW(format("{1}}+{2} = {0}", "2", "one"), InvalidBracketsException);
};


TEST(FormatTest, Test4)
{
    EXPECT_THROW(format("{1asdasd}+{2} = {0}", "2", "one"), TokenException);
};


TEST(FormatTest, Test5)
{
    EXPECT_THROW(format("{1", "one", "two"), InvalidBracketsException);
};

