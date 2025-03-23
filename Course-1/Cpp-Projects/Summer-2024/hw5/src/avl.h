// Copyright 2024 <Dyshnui linter inc.>
#pragma once
#include <algorithm>
#include <cmath>
#include <memory>
#include <initializer_list>
#include <vector>
#include <stack>
#include <exception>
#include <functional>
#include <utility>


template <class Key, class T>
class Node {
 public:
    Key key;
    T value;
    Node<Key, T>* left;
    Node<Key, T>* right;
    int height;
    Node() : left{ nullptr }, right{ nullptr }, height{ 1 } {}
    Node(const Key& key, const T& value) : key{ key }, value{ value },
    left{ nullptr }, right{ nullptr }, height{ 1 } {}
};


template <class Key, class T>
class AllocatorAVL {
    size_t offset;
    size_t capacity;
    Node<Key, T>* bufferBase;
    std::stack<Node<Key, T>*> removed;

 public:
    Node<Key, T>* allocate(const Key& key, const T& value) {
        if (offset + 1 >= capacity && removed.empty()) {
            throw std::runtime_error("Insufficient memory buffer");
        }
        Node<Key, T>* res;
        if (!removed.empty()) {
            res = removed.top();
            removed.pop();
        } else {
            res = bufferBase + sizeof(Node<Key, T>) * offset;
            offset++;
        }
        res->key = key;
        res->value = value;
        return res;
    }
    void deallocate(Node<Key, T>* ptr) {
        if (ptr == nullptr) {
            throw std::runtime_error("Unable to deallocate");
        }
        ptr->height = 1;
        ptr->left = nullptr;
        ptr->right = nullptr;
        removed.push(ptr);
    }
    AllocatorAVL() : offset{ 0 }, capacity{ 1000 } {
        bufferBase = new Node<Key, T>[capacity];
    }
    explicit AllocatorAVL(size_t capacity) : offset{ 0 }, capacity{ capacity } {
        bufferBase = new Node<Key, T>[capacity];
    }
    ~AllocatorAVL() {
        delete[] bufferBase;
    }
};


template <class Key, class T>
class IteratorAVL;


template <class Key, class T, class Compare = std::less<Key>,
class Allocator = AllocatorAVL<Key, T>>
class AVL {
    friend class IteratorAVL<Key, T>;
    Node<Key, T>* treeRoot;
    std::vector<Node<Key, T>*> treeTraverse;
    size_t treeSize;
    bool isChanched;
    Allocator treeAllocator;
    void rebuildTraverse() {
        if (!isChanched) {
            return;
        }
        isChanched = false;
        treeTraverse.clear();
        treeTraverse.push_back(nullptr);
        traverseTree(treeRoot);
    }
    void traverseTree(Node<Key, T>* v) {
        if (v->left != nullptr) {
            traverseTree(v->left);
        }
        if (v->right != nullptr) {
            traverseTree(v->right);
        }
        treeTraverse.push_back(v);
    }
    int getHeight(Node<Key, T>* node) const {
        if (node == nullptr) {
            return 0;
        }
        return node->height;
    }
    void calcHeight(Node<Key, T>* node) const {
        node->height = std::max(getHeight(node->left),
        getHeight(node->right)) + 1;
    }
    Node<Key, T>* rRotate(Node<Key, T>* x) {
        Node<Key, T>* y = x->left;
        x->left = y->right;
        y->right = x;
        calcHeight(x);
        calcHeight(y);
        return y;
    }
    Node<Key, T>* lRotate(Node<Key, T>* y) {
        Node<Key, T>* x = y->right;
        y->right = x->left;
        x->left = y;
        calcHeight(y);
        calcHeight(x);
        return x;
    }
    int balanceFactor(Node<Key, T>* node) const {
        return getHeight(node->right) - getHeight(node->left);
    }
    Node<Key, T>* balance(Node<Key, T>* node) {
        calcHeight(node);
        Node<Key, T>* res;
        if (balanceFactor(node) == 2) {
            if (balanceFactor(node->right) < 0) {
                node->right = rRotate(node->right);
            }
            res = lRotate(node);
        } else if (balanceFactor(node) == -2) {
            if (balanceFactor(node->left) > 0) {
                node->left = lRotate(node->left);
            }
            res = rRotate(node);
        } else {
            res = node;
        }
        if (node == treeRoot) {
            treeRoot = res;
        }
        return res;
    }
    Node<Key, T>* findMin(Node<Key, T>* node) const {
        if (node->left != nullptr) {
            return findMin(node->left);
        }
        return node;
    }
    Node<Key, T>* extractMin(Node<Key, T>* node) {
        if (node->left == nullptr) {
            return node->right;
        }
        node->right = extractMin(node->left);
        return balance(node);
    }
    void removeVertex(Node<Key, T>* node) {
        if (node->left != nullptr) {
            removeVertex(node->left);
            node->left = nullptr;
        }
        if (node->right != nullptr) {
            removeVertex(node->right);
            node->right = nullptr;
        }
        treeSize--;
    }

 public:
    T& at(const Key& key) const {
        Node<Key, T>* res = find(key);
        if (res == nullptr) {
            throw std::runtime_error("Index error");
        }
        return res->value;
    }
    T& operator[](const Key& key) const {
        Node<Key, T>* res = find(key);
        if (res == nullptr) {
            throw std::runtime_error("Index error");
        }
        return res->value;
    }
    Node<Key, T>* insert(const std::pair<Key, T>& elem) {
        isChanched = true;
        return insert(elem, treeRoot);
    }
    Node<Key, T>* insert(const std::pair<Key, T>& elem, Node<Key, T>* root) {
        if (root == nullptr) {
            isChanched = true;
            treeSize++;
            auto newNode = treeAllocator.allocate(elem.first, elem.second);
            if (treeRoot == nullptr) {
                treeRoot = newNode;
            }
            return newNode;
        }
        if (elem.first < root->key) {
            root->left = insert(elem, root->left);
        } else {
            root->right = insert(elem, root->right);
        }
        return balance(root);
    }
    Node<Key, T>* erase(const Key& key) {
        isChanched = true;
        return erase(key, treeRoot);
    }
    Node<Key, T>* erase(const IteratorAVL<Key, T>& iter) {
        isChanched = true;
        return erase((*iter).key, treeRoot);
    }
    Node<Key, T>* erase(const Key& key, Node<Key, T>* node) {
        if (node == nullptr) {
            return nullptr;
        }
        if (key < node->key) {
            node->left = erase(key, node->left);
        } else if (key > node->key) {
            node->right = erase(key, node->right);
        } else {
            isChanched = true;
            Node<Key, T>* left = node->left;
            Node<Key, T>* right = node->right;
            treeAllocator.deallocate(node);
            if (treeSize == 1) {
                treeRoot = nullptr;
            }
            treeSize--;
            if (right == nullptr) {
                return left;
            }
            auto min = findMin(right);
            min->right = extractMin(right);
            min->left = left;
            if (node == treeRoot) {
                treeRoot = min;
            }
            return balance(min);
        }
        return balance(node);
    }
    Node<Key, T>* find(const Key& key) const {
        return find(key, treeRoot);
    }
    Node<Key, T>* find(const Key& key, Node<Key, T>* root) const {
        if (root == nullptr || key == root->key) {
            return root;
        }
        if (key < root->key) {
            return find(key, root->left);
        } else {
            return find(key, root->right);
        }
    }
    bool contains(const Key& key) const {
        return find(key) != nullptr;
    }
    bool empty() const {
        return treeSize == 0;
    }
    size_t size() const {
        return treeSize;
    }
    void clear() {
        isChanched = true;
        removeVertex(treeRoot);
        treeRoot = nullptr;
    }
    IteratorAVL<Key, T> begin() {
        rebuildTraverse();
        IteratorAVL<Key, T> iter(this, 1, false);
        return iter;
    }
    IteratorAVL<Key, T> rbegin() {
        rebuildTraverse();
        IteratorAVL<Key, T> iter(this, treeSize, true);
        return iter;
    }
    IteratorAVL<Key, T> end() {
        rebuildTraverse();
        IteratorAVL<Key, T> iter(this, treeSize + 1, false);
        return iter;
    }
    IteratorAVL<Key, T> rend() {
        rebuildTraverse();
        IteratorAVL<Key, T> iter(this, 0, true);
        return iter;
    }
    AVL(const std::initializer_list <std::pair<Key, T>> init) : treeSize{ 0 },
    isChanched{ false } {
        for (auto it = init.begin(); it != init.end(); it++) {
            insert(*it);
        }
    }
    AVL() : treeSize{ 0 }, treeRoot{ nullptr }, isChanched{ false } {}
};


template <class Key, class T>
class IteratorAVL {
    AVL<Key, T>* tree;
    int traverseIdx;
    bool reversed;

 public:
    IteratorAVL& operator++() {
        if (reversed) {
            traverseIdx--;
        } else {
            traverseIdx++;
        }
        return *this;
    }
    IteratorAVL& operator--() {
        if (reversed) {
            traverseIdx++;
        } else {
            traverseIdx--;
        }
        return *this;
    }
    IteratorAVL& operator++(int) {
        if (reversed) {
            traverseIdx--;
        } else {
            traverseIdx++;
        }
        return *this;
    }
    IteratorAVL& operator--(int) {
        if (reversed) {
            traverseIdx++;
        } else {
            traverseIdx--;
        }
        return *this;
    }
    Node<Key, T> operator*() {
        return *tree->treeTraverse[traverseIdx];
    }
    bool operator==(const IteratorAVL<Key, T>& other) {
        return (traverseIdx == other.traverseIdx) &&
        (reversed == other.reversed);
    }
    bool operator!=(const IteratorAVL<Key, T>& other) {
        return !(*this == other);
    }
    IteratorAVL(AVL<Key, T>* tree, int traverseIdx, bool reversed) :
    tree{ tree }, traverseIdx{ traverseIdx },
    reversed{ reversed } {}
};
