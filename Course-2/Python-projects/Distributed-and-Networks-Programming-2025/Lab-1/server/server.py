import argparse
import os
import socket
import time


MSS = 20480


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("server_port", type=str)
    args = parser.parse_args()
    server_port = args.server_port

    server_sock = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
    server_address = ("0.0.0.0", int(server_port))
    server_sock.bind(server_address)
    server_sock.settimeout(5.0)

    file = None
    filename = None
    filesize = 0
    bytes_recv = -1
    prev_seqno = 1

    while True:
        try:
            data, client_address = server_sock.recvfrom(MSS)
            message = data.split(b'|')
            op_type, seqno = message[0].decode(), int(message[1].decode())

            if prev_seqno == seqno or bytes_recv >= filesize:
                packet = f"a|{(seqno + 1) % 2}".encode()
                server_sock.sendto(packet, client_address)
                continue

            if op_type == "s":
                filename = message[2].decode()
                filesize = int(message[3].decode())
                if os.path.exists("server/" + filename):
                    print(
                        f'''File {
                            "server/" +
                            filename} already exists. Its' content will be rewritten''')
                    os.remove("server/" + filename)

            elif op_type == "d":
                bin_message = data[4:]
                bytes_recv += len(bin_message)
                file = open("server/" + filename, mode="ab")
                file.write(bin_message)
                file.close()

            else:
                raise ValueError("Wrong packet lable received")

            prev_seqno = seqno

            packet = f"a|{(seqno + 1) % 2}".encode()
            server_sock.sendto(packet, client_address)

        except socket.timeout as e:
            if filesize >= bytes_recv:
                print("The file was fully received")
            else:
                print(str(e))
            break

        except Exception as e:
            print(str(e))
            break
