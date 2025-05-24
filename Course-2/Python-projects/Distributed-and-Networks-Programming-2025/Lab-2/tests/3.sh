#!/bin/bash

echo "-------------------"
echo "Test 3: Sequence clients run"
echo "-------------------"

# Check file existence
echo "Checking if server.py exists in the correct location"
if ! test -f task_multithreading/server.py; then
    echo "File does not exist: task_multithreading/server.py"
    exit 1
fi

echo "Running server in background"
python3 task_multithreading/server.py 8000 & PID=$!
sleep 2

echo "Running sequence of clients"
RES=$(python3 task_multithreading/client.py 127.0.0.1:8000 3 --number 1 && python3 task_multithreading/client.py 127.0.0.1:8000 5 --number 2 && python3 task_multithreading/client.py 127.0.0.1:8000 7 --number 3)

echo ""
echo "$RES"

if ! [[ $RES =~ "mean_data 3.0" ]]; then
    echo "no mean_data or it is wrong"
    kill -9 $PID
    exit 1
fi

if ! [[ $RES =~ "mean_data 4.0" ]]; then
    echo "no mean_data or it is wrong"
    kill -9 $PID
    exit 1
fi

if ! [[ $RES =~ "mean_data 5.0" ]]; then
    echo "no mean_data or it is wrong"
    kill -9 $PID
    exit 1
fi

kill -9 $PID
