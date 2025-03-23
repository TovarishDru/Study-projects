// Copyright 2024 <Dyshnui linter inc.>
#include <gtest/gtest.h>
#include "avl.h"

TEST(TreeTest, CommonHeightTest) {
    AVL<int, int> tree;
    tree.insert({ 1, 5 });
    tree.insert({ 2, 5 });
    tree.insert({ 3, 5 });
    tree.insert({ 4, 5 });
    tree.insert({ 5, 5 });
    tree.insert({ 6, 5 });
    for (auto it = tree.begin(); it != tree.end(); it++) {
        std::cout << (*it).key;
        if ((*it).left == nullptr) {
            std::cout << " null";
        } else {
            std::cout << " " << (*it).left->key;
        }
        if ((*it).right == nullptr) {
            std::cout << " null ";
        } else {
            std::cout << " " << (*it).right->key << " ";
        }
        std::cout << "Height " << (*it).height << "\n";
    }
    EXPECT_EQ((*tree.rbegin()).height, 3);
}


TEST(TreeTest, SameValuesHeightTest) {
    AVL<int, int> tree;
    tree.insert({ 3, 1 });
    tree.insert({ 3, 2 });
    tree.insert({ 3, 3 });
    for (auto it = tree.begin(); it != tree.end(); it++) {
        std::cout << (*it).key;
        if ((*it).left == nullptr) {
            std::cout << " null";
        } else {
            std::cout << " " << (*it).left->key;
        }
        if ((*it).right == nullptr) {
            std::cout << " null ";
        } else {
            std::cout << " " << (*it).right->key << " ";
        }
        std::cout << "Height " << (*it).height << "\n";
    }
    EXPECT_EQ((*tree.rbegin()).height, 2);
}


TEST(TreeTest, EraseTest) {
    AVL<int, int> tree;
    tree.insert({ 1, 5 });
    tree.insert({ 2, 5 });
    tree.insert({ 3, 5 });
    tree.insert({ 4, 5 });
    tree.insert({ 5, 5 });
    tree.insert({ 6, 5 });
    tree.erase(4);
    for (auto it = tree.begin(); it != tree.end(); it++) {
        std::cout << (*it).key;
        if ((*it).left == nullptr) {
            std::cout << " null";
        } else {
            std::cout << " " << (*it).left->key;
        }
        if ((*it).right == nullptr) {
            std::cout << " null ";
        } else {
            std::cout << " " << (*it).right->key << " ";
        }
        std::cout << "Height " << (*it).height << "\n";
    }
    EXPECT_EQ((*tree.rbegin()).key, 5);
}


TEST(TreeTest, EraseAndInsertTest) {
    AVL<int, int> tree;
    tree.insert({ 1, 5 });
    tree.insert({ 2, 5 });
    tree.insert({ 3, 5 });
    tree.insert({ 4, 5 });
    tree.insert({ 5, 5 });
    tree.insert({ 6, 5 });
    tree.erase(4);
    tree.erase(5);
    tree.insert({ 10, 5 });
    tree.insert({ 11, 5 });
    for (auto it = tree.begin(); it != tree.end(); it++) {
        std::cout << (*it).key;
        if ((*it).left == nullptr) {
            std::cout << " null";
        } else {
            std::cout << " " << (*it).left->key;
        }
        if ((*it).right == nullptr) {
            std::cout << " null ";
        } else {
            std::cout << " " << (*it).right->key << " ";
        }
        std::cout << "Height " << (*it).height << "\n";
    }
    EXPECT_EQ((*tree.rbegin()).height, 3);
}


TEST(TreeTest, atTest) {
    AVL<int, int> tree;
    tree.insert({ 1, 4 });
    tree.insert({ 2, 5 });
    tree.insert({ 3, 6 });
    EXPECT_EQ(tree.at(2), 5);
}


TEST(TreeTest, bracketsOperatorTest) {
    AVL<int, int> tree;
    tree.insert({ 1, 4 });
    tree.insert({ 2, 5 });
    tree.insert({ 3, 6 });
    EXPECT_EQ(tree[2], 5);
}


TEST(TreeTest, findTest) {
    AVL<int, int> tree;
    tree.insert({ 1, 4 });
    tree.insert({ 2, 5 });
    tree.insert({ 3, 6 });
    EXPECT_EQ(tree.find(2)->value, 5);
}
