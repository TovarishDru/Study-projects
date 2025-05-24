#!/bin/bash

echo "-------------------"
echo "Test 4: Parallel clients run"
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

echo "Running parallel of clients"
python3 task_multithreading/client.py 127.0.0.1:8000 3 --number 1 > RES1_file &
python3 task_multithreading/client.py 127.0.0.1:8000 5 --number 2 > RES2_file &
python3 task_multithreading/client.py 127.0.0.1:8000 7 --number 3 > RES3_file &

sleep 7

RES1=$(cat RES1_file)
RES2=$(cat RES2_file)
RES3=$(cat RES3_file)

echo ""
echo "$RES1"
echo ""
echo "$RES2"
echo ""
echo "$RES3"

if ! [[ $RES1 =~ "mean_data 5.0" ]]; then
    echo "no mean_data or it is wrong"
    kill -9 $PID
    exit 1
fi

if ! [[ $RES2 =~ "mean_data 5.0" ]]; then
    echo "no mean_data or it is wrong"
    kill -9 $PID
    exit 1
fi

if ! [[ $RES3 =~ "mean_data 5.0" ]]; then
    echo "no mean_data or it is wrong"
    kill -9 $PID
    exit 1
fi

kill -9 $PID
