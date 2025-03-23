#pragma once


class Allocator
{
    char* bufferBase;
    size_t offset;
    size_t capacity;
public:
    void makeAllocator(size_t);
    char* alloc(size_t);
    void reset();
    Allocator();
    ~Allocator();
};