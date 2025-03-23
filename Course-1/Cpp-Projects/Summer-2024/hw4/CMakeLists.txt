cmake_minimum_required(VERSION 3.10)
project(MyLibrary)

# Set the C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
add_compile_options(-g)

set(SOURCE_FILES
        src/format.cpp
)

include_directories(src)

add_library(MyLibrary ${SOURCE_FILES})

find_package(GTest REQUIRED)
include_directories(${GTEST_INCLUDE_DIRS})

add_executable(
        format_test
        tests/format_test.cpp
)

target_link_libraries(
        format_test
        GTest::gtest_main
)

target_link_libraries(
        format_test
        MyLibrary
)
