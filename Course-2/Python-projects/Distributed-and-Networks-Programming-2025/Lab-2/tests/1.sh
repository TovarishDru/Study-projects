#!/bin/bash

echo "-------------------"
echo "Test 1: Essentials"
echo "-------------------"

# Check file existence
echo "Checking if server.py exists in the correct location"
if ! test -f task_multithreading/server.py; then
    echo "File does not exist: task_multithreading/server.py"
    exit 1
fi

# Installing autopep8
echo "Installing autopep8"
if ! pip install --no-cache-dir autopep8; then
    echo "Failed to install autopep8"
    exit 1
fi

# Checking code formatting
if ! autopep8 --exit-code -i -a task_multithreading/server.py; then
    echo "Your source code at task_multithreading/server.py is not formatted according to PEP8"
    echo "Run 'autopep8 -i -a task_multithreading/server.py' to fix"
    exit 1
fi

# Check file existence
echo "Checking if optimized.py exists in the correct location"
if ! test -f task_multiprocessing/optimized.py; then
    echo "File does not exist: task_multiprocessing/optimized.py"
    exit 1
fi

# Checking code formatting
if ! autopep8 --exit-code -i -a task_multiprocessing/optimized.py; then
    echo "Your source code at task_multiprocessing/optimized.py is not formatted according to PEP8"
    echo "Run 'autopep8 -i -a task_multiprocessing/optimized.py' to fix"
    exit 1
fi

# Check file existence
echo "Checking if optimized.py exists in the correct location"
if ! test -f task_multiprocessing/nonoptimized.py; then
    echo "File does not exist: task_multiprocessing/nonoptimized.py"
    exit 1
fi

# Checking code formatting
if ! autopep8 --exit-code -i -a task_multiprocessing/nonoptimized.py; then
    echo "Your source code at task_multiprocessing/nonoptimized.py is not formatted according to PEP8"
    echo "Run 'autopep8 -i -a task_multiprocessing/nonoptimized.py' to fix"
    exit 1
fi
