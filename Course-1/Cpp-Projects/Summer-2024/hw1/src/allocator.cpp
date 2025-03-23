#include <memory>
#include "allocator.h"


void Allocator::makeAllocator(size_t maxSize)
{
    if (maxSize <= 0)
    {
        return;
    }
    reset();
    delete[] bufferBase;
    bufferBase = new char[maxSize];
    capacity = maxSize;

}


char* Allocator::alloc(size_t size)
{
    if ((size <= 0) || (offset + size > capacity))
    {
        return nullptr;
    }
    char* allocatedPtr = bufferBase + offset;
    offset += size;
    return allocatedPtr;
}


void Allocator::reset()
{
    offset = 0;
}


Allocator::Allocator() : bufferBase(nullptr), capacity(0), offset(0) {}

Allocator::~Allocator() {
    delete[] bufferBase;
}