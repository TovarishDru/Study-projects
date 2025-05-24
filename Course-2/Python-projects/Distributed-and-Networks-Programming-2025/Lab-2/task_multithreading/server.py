import argparse
import socket
from threading import Thread


SERVER_BUFFER = 1024
sum = 0
num = 0


def serve_client(client_sock, client_addr):
    global sum, num
    data = client_sock.recv(SERVER_BUFFER).decode()
    num += 1
    sum += int(data)

    data = client_sock.recv(SERVER_BUFFER).decode()
    client_sock.send(str(float(sum / num)).encode())

    client_sock.close()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("server_port", type=str)
    args = parser.parse_args()
    server_port = int(args.server_port)

    server_sock = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
    server_addr = ("0.0.0.0", server_port)
    server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_sock.bind(server_addr)
    server_sock.listen()

    while True:
        try:
            client_sock, client_addr = server_sock.accept()
            thread = Thread(
                target=serve_client, args=(
                    client_sock, client_addr))
            thread.start()
        except Exception as e:
            print(str(e))
            break


if __name__ == "__main__":
    main()
