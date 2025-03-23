cmake_minimum_required(VERSION 3.10)
project(MyLibrary)

# Set the C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
add_compile_options(-g)

set(SOURCE_FILES
        src/allocator.cpp
)

include_directories(src)

add_library(MyLibrary ${SOURCE_FILES})

find_package(GTest REQUIRED)
include_directories(${GTEST_INCLUDE_DIRS})

add_executable(
	allocator_test
	tests/allocator_test.cpp
)

target_link_libraries(
	allocator_test
  	GTest::gtest_main
)

target_link_libraries(
        allocator_test
        MyLibrary
)
