#!/bin/bash

echo "-------------------"
echo "Test 5: Multiprocessing task"
echo "-------------------"

# Check file existence
echo "Checking if optimized.py exists in the correct location"
if ! test -f task_multiprocessing/optimized.py; then
    echo "File does not exist: task_multiprocessing/optimized.py"
    exit 1
fi

# Check file existence
echo "Checking if nonoptimized.py exists in the correct location"
if ! test -f task_multiprocessing/nonoptimized.py; then
    echo "File does not exist: task_multiprocessing/nonoptimized.py"
    exit 1
fi

echo "Running nonoptimized solution"
nonoptimized_start=$SECONDS
python3 task_multiprocessing/nonoptimized.py
nonoptimized_end=$SECONDS
let "nonoptimized_time = nonoptimized_end - nonoptimized_start"
sleep 2

echo "Running optimized solution"
optimized_start=$SECONDS
python3 task_multiprocessing/optimized.py
optimized_end=$SECONDS
let "optimized_time = optimized_end - optimized_start"
sleep 2

if [[ $optimized_time -gt $nonoptimized_time ]]; then
    echo "Your optimized solution is worse than nonoptimized"
    exit 1
fi

echo "Checking results"
if ! cmp --silent task_multiprocessing/primes.txt primes_optimized.txt; then
    echo "Files task_multiprocessing/primes.txt and primes_optimized.txt do not match"
    exit 1
fi

if ! cmp --silent task_multiprocessing/primes.txt primes_nonoptimized.txt; then
    echo "Files task_multiprocessing/primes.txt and primes_nonoptimized.txt do not match"
    exit 1
fi