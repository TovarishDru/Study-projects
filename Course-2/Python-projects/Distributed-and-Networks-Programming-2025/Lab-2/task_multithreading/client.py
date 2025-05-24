import argparse
import socket
import time

CLIENT_BUFFER = 1024


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("server_addr", type=str)
    parser.add_argument("data", type=int)
    parser.add_argument("--number", type=int, default=0)
    args = parser.parse_args()

    server_ip, server_port = args.server_addr.split(":")
    server_port = int(server_port)

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        print(
            f"Client {args.number}: connecting to {(server_ip, server_port)}")
        s.connect((server_ip, server_port))

        print(f"Client {args.number}: sending data: {args.data}")
        s.send(str(args.data).encode())

        print(f"Client {args.number}: sleeping for 5 seconds")
        time.sleep(5)

        print(f"Client {args.number}: sending ready message")
        s.send(b"ready")

        print(f"Client {args.number}: receving mean data")
        mean_data = float(s.recv(CLIENT_BUFFER).decode())
        print(f"Client {args.number}: mean_data {mean_data}")


if __name__ == "__main__":
    main()
