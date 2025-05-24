# Week 1 - [Stop-and-Wait ARQ]

> Distributed Systems and Network Programming - Spring 2025

## Task

Your task for this lab is to **write a UDP server** using Python socket module that communicates with the client.

The client reads a file from the local file system and uploads it to the server.

### Client Implementation

- The client starts by sending a message in the format: `s|0|filename.ext|filesize`, where:
  - `s` indicates that the message type is "start"
  - `0` is the initial sequence number.
  - `filename.ext` is the name of the file to be sent (with extension).
  - `filesize` is the total size of the file to be sent (in bytes).
- The client expects a reply from the server in the format `a|seqno` where:
  - `a` indicates that the message type is `acknowledgement`.
  - `seqno` equals `(x+1)%2` where `x` is the sequence number of the message to be acknowledged.
- If the expected server acknowledgement was received successfully, the client does the following:
  1. Split the file into chunks so that the size of a single data packet (including headers) does not exceed the buffer size of the server.
  1. Start sending file chunks, one by one. Each chunk has the format `d|seqno|data` where:
     - `d` indicates that the message type is "data"
     - `seqno` is the sequence number of the data message, it alternates between `1` and `0`, starting from `1`.
     - `data` is the raw bytes of the file.
  1. Wait for an acknowledgement message after sending each chunk.
- If an expected acknowledgement message does not arrive within 1 second, the client retransmits the message.
- If an acknowledgement message arrives with an unexpected sequence number, the client ignores that duplicate ACK and keeps waiting for the expected ACK.

### Server Implementation

1. Parse one integer argument, the port number to listen on.
1. Create a UDP socket and start listening for incoming messages on `0.0.0.0:<port>`.
   - The server should use a fixed receiver buffer size of `20480` bytes (20 Kibibytes).
1. Upon receiving a message from a client, inspect the message type (first character).

   - If the message type is `s`, prepare to receive a file from the client with the given name and size.
   - If the message type is `d`, write the delivered chunk to the file system.
   - Otherwise, terminate gracefully with an error.
1. In both cases, reply with an acknowledge message in the format `a|seqno` where

   - `a` indicates that the message type is `acknowledgement`.
   - `seqno` equals `(x+1)%2` where `x` is the sequence number of the message to be acknowledged.
1. Once the file is received completely, the server should print an indicating message, write the content to the file system, and close the file.
1. If an existing file with the same name is present in the server directory, the server should print an indicating message and overwrite that file with the new one.
1. The server stays running unless a fatal error occurs or a `KeyboardInterrupt` is received.

> Your server will be tested under constant delay and packet loss. The following Linux command can be used to simulate 15% packet loss and 1100 milliseconds constant delay over the `lo` interface. File transfer should still succeed after applying the command.
>
> ```bash
> sudo tc qdisc add dev lo root netem loss 15% delay 1100ms
> ```
>
> To undo the effect use `del` instead of `add`.
