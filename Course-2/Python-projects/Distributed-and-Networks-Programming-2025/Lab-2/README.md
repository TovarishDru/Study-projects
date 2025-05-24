# Week 2 - Concurrency and Parallelism

> Distributed and Networking Programming - Spring 2025

Your tasks for this lab:

1. Multithreading: write a multi-threaded TCP server that communicates with a given client;
2. Multiprocessing: compute prime numbers using multiprocessing.


## Task: multithreading

The server should do the following:

1. Accept a new connection from a client;
2. Spawn a new thread to handle the connection;
3. Wait for client to send data;
4. Recalculate the mean for all data from clients that has been already sent;
4. Wait for ready message from the client; 
5. Send the mean of all received data from all clients.

Additional requirements:

- The server should stay listening all the time and should not terminate unless a `KeyboardInterrupt` is received;
- The server should be able to handle multiple connections simultaneously;
- The server socket should be marked for address reuse so that the OS would immediately release the bound address after server termination. 

### Client Implementation

The client does the following:

1. Connects to the TCP server;
2. Sends user-provided data (passed as argument, see examples);
3. Waits 5 seconds then sends "ready" message;
4. Receives "mean" data from the server.


## Task: multiprocessing

Find all prime numbers amongst first 1,000,000 numbers using multiprocessing. You should provide both single-processed solution and "optimized" one (multiprocessed).

Instructions

1. Write basic single-process solution (the most basic one - bruteforce, **do not use** Sieve of Eratosthenes or some other optimizations) in `nonoptimized.py` file;
2. Modify the script to use multiprocessing to process multiple numbers concurrently and write your solution into `optimized.py` file;
3. Ensure that first solution write primes to `primes_nonoptimized.txt` file, while second one to `primes_optimized.txt` (and also there shouldn't be a new line `\n` at the end of the file, check `primes.txt` for reference);
4. Compare the execution time of the optimized script with the original version (it should decrease);
