BUILDDIR = build

TEST_EXEC = $(BUILDDIR)/allocator_test

all: build

build:
	cmake -B$(BUILDDIR) -H.
	$(MAKE) -C $(BUILDDIR)

test: build
	$(TEST_EXEC)

memory: build
	valgrind --leak-check=full $(TEST_EXEC)

format: 
	find . -iname "*.h" -o -iname "*.cpp" | xargs clang-format -i --Werror

lint: 
	cpplint --filter=-legal/copyright,-build/include_subdir --recursive . 

check:
	cppcheck --enable=all --suppress=missingIncludeSystem --checkers-report=out.txt -I src .

clean:
	$(RM) -r $(BUILDDIR)

.PHONY: all build test memory format lint check clean 
