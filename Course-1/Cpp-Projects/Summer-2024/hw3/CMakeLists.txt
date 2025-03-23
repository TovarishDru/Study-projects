cmake_minimum_required(VERSION 3.10)
project(MyLibrary)

# Set the C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
add_compile_options(-g)

set(SOURCE_FILES
        src/long_arithmetics.cpp
)


include_directories(${CMAKE_SOURCE_DIR})


add_library(MyLibrary ${SOURCE_FILES})

find_package(GTest REQUIRED)
include_directories(${GTEST_INCLUDE_DIRS})

add_executable(
	long_arithmetics_test # имя теста, можно любое (но если поменяли, то дальше нужно тоже изменить)
        tests/long_arithmetics_test.cpp
)

target_link_libraries(
        long_arithmetics_test
  	GTest::gtest_main
)

target_link_libraries(
        long_arithmetics_test
        MyLibrary
)
