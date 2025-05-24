import socket
from threading import Thread
from log_meth.log import log_warn, log_error, log_info
from classes.Database import Database
import json
from constants.Protocols import Request, Response
from constants.Constants import WHITE, BLACK
from classes.Board import Board
import random
import traceback
import hashlib


IP = "127.0.0.1"
PORT = 8080
SERVER_BUFFER = 8192


class Client:
    def __init__(self, host_sock, host_addr, host_name, host_id):
        self.host_sock = host_sock
        self.host_addr = host_addr
        self.host_name = host_name
        self.host_id = host_id


class Game:
    def __init__(self, game_id):
        self.clients = [None, None]
        self.connected = 0
        self.game_id = game_id
        self.finished = False
        self.board = Board()

    def join_game(self, client):
        if client in self.clients:
            return False
        if self.connected < 2:
            self.clients[self.connected] = client
            self.connected += 1
            return True
        return False
    
    def check_winner(self):
        if self.board.is_checkmate():
            return self.board.current_player_color()
        return None


class GameServer:
    def __init__(self, sock, addr):
        self.games = []
        self.games_count = 0
        self.sock = sock
        self.addr = addr
        self.connected_clients = dict()
        self.master_database = Database()
        self.master_database._initialize_db()

    def close_database(self):
        self.master_database.close()

    def create_game(self):
        game_id = self.games_count
        self.games_count += 1
        game = Game(game_id)
        self.games.append(game)
        log_info(f"Created new game with ID {game_id}")
        return game
    
    def connect_client(self, host_sock, host_addr, host_name):
        host_id = hash(host_name)
        self.connected_clients[host_name] = Client(host_sock, host_addr, host_name, host_id)
        return host_id
    
    def check_connection(self, host_name, host_id=None):
        if host_name not in self.connected_clients:
            return False
        if host_id is None:
            return True
        if self.connected_clients[host_name].host_id == host_id:
            return True
        return False
    
    def remove_connection(self, host_name, host_id):
        if self.check_connection(host_name, host_id):
            del self.connected_clients[host_name]

    def remove_game(self, game_id):
        for i in range(len(self.games)):
            if self.games[i].game_id == game_id:
                del self.games[i]
                self.games_count -= 1
                break
        log_info(f"Removed the game: {game_id}.")

    def serve_connection(self, client_sock, client_addr):
        log_info(f"New client connected: {client_addr}")
        database = Database()
        host_id = None
        username = None
        while True:
            try:
                message = client_sock.recv(SERVER_BUFFER)
                log_info(f"Message was received from client: {client_addr}")
                log_info(message)
                data = json.loads(message.decode('utf-8'))
                response = dict()
                log_info(f"Message was decoded.")

                if int(data['type']) == Request.GET_USER_DATA.value:
                    username = data['username']
                    host_id = data['host_id']
                    log_info(f"User {username} requested their data list.")
                    if not self.check_connection(username, host_id):
                        raise ValueError("User is not connected.")
                    res = database.get_user(username)
                    if res is None:
                        raise ValueError("User is not present in the database.")
                    else:
                        response['type'] = Response.USER_DATA.value
                        response['wins'] = res[3]
                        response['losses'] = res[4]
                        response['total_matches'] = res[5]
                    json_response = json.dumps(response).encode("utf-8")
                    client_sock.send(json_response)
                    log_info(f"The data list was sent to user {username}.")
                    
                elif int(data['type']) == Request.AUTHENTICATE.value:
                    username = data['username']
                    password = data['password']
                    log_info(f"Authentication was requseted by {username}.")
                    if (database.verify_user(username, password)):
                        if self.check_connection(username):
                            log_warn(f"User {username} is already connected.")
                            response['type'] = Response.UNAUTHORIZED.value
                        else:
                            host_id = self.connect_client(client_sock, client_addr, username)
                            response['type'] = Response.AUTHORIZED.value
                            response['host_id'] = host_id
                            log_info(f"Authentication was approved to {username}.")
                    else:
                        response['type'] = Response.UNAUTHORIZED.value
                        log_warn(f"Unable to authenticate {username}.")
                    json_response = json.dumps(response).encode("utf-8")
                    client_sock.send(json_response)
                    
                elif int(data['type']) == Request.CREATE_USER.value:
                    username = data['username']
                    password = data['password']
                    log_info(f"User creation was requseted by {username}.")
                    if database.get_user(username):
                        raise ValueError('User with such nickname already exists.')
                    elif database.add_user(username, password):
                        host_id = self.connect_client(client_sock, client_addr, username)
                        response['type'] = Response.AUTHORIZED.value
                        response['host_id'] = host_id
                        log_info(f"User {username} was created.")
                    else:
                        raise ValueError(f"Unable to create user {username}.")
                    json_response = json.dumps(response).encode("utf-8")
                    client_sock.send(json_response)

                elif int(data['type']) == Request.GET_GAMES.value:
                    page_num_l = int(data["page_num_l"])
                    page_num_r = int(data["page_num_r"])
                    username = data['username']
                    host_id = data['host_id']
                    log_info(f"User {username} requested the game list.")
                    if not self.check_connection(username, host_id):
                        raise ValueError("User is not connected.")
                    if page_num_l < 0 or page_num_l > self.games_count or page_num_r < 0:
                        raise IndexError("Invalid page range: left or right index out of bounds")
                    response["type"] = Response.RETURN_GAMES.value
                    response["data"] = [(self.games[i].game_id, self.games[i].clients[0].host_name) for i in range(page_num_l, min(page_num_r, self.games_count))]
                    json_response = json.dumps(response).encode("utf-8")
                    client_sock.send(json_response)
                    log_info(f"The game list was sent to user {username}.")
    
                elif int(data['type']) == Request.REMOVE_THE_GAME.value:
                    game_id = int(data['game_id'])
                    host_id = data["host_id"]
                    username = data['username']
                    game = self.games[game_id]
                    log_info(f"User {username} requested game {game_id} removal.")
                    if not self.check_connection(username, host_id):
                        raise ValueError("User is not connected")
                    if not any(client and client.host_id == host_id for client in game.clients):
                        raise ValueError(f"Invalid client ID {host_id} for game {game_id}")
                    if not game.finished:
                        for i, client in enumerate(game.clients):
                            if client is not None and client.host_addr != client_addr:
                                win_resp = {
                                    "type": Response.GAME_FINISHED_TECH.value,
                                }
                                client.host_sock.send(json.dumps(win_resp).encode("utf-8"))
                    self.remove_game(game_id)
                    log_info(f"Removed the game: {game_id}.")

                elif int(data['type']) == Request.CONNECT_TO_GAME.value:
                    host_id = data['host_id']
                    game_id = int(data['game_id'])
                    username = data['username']
                    log_info(f"User {username} requested connection to game {game_id}.")
                    if not self.check_connection(username, host_id):
                        raise ValueError("User is not connected")
                    if game_id < 0 or game_id >= self.games_count:
                        raise IndexError(f"Game ID {game_id} does not exist")
                    if self.games[game_id].connected >= 2:
                        raise RuntimeError(f"Game {game_id} is already full")
                    client = self.connected_clients[username]
                    res = self.games[game_id].join_game(client)
                    if not res:
                        raise RuntimeError(f"Failed to connect client to game {game_id}")
                    log_info(f"Client {client_addr} connected to game {game_id}")
                    creator = self.games[game_id].clients[0].host_sock
                    creator_response = {"type": Response.START_GAME.value}
                    creator.send(json.dumps(creator_response).encode("utf-8"))
                    response["type"] = Response.CONNECT_TO_GAME.value
                    client_sock.send(json.dumps(response).encode("utf-8"))

                elif int(data["type"]) == Request.CREATE_THE_GAME.value:
                    host_id = data['host_id']
                    username = data['username']
                    log_info(f"User {username} requested creation of game.")
                    if not self.check_connection(username, host_id):
                        raise ValueError("User is not connected")
                    game = self.create_game()
                    client = self.connected_clients[username]
                    game_id = game.game_id
                    res = self.games[game_id].join_game(client)
                    if not res:
                        self.remove_game(game_id)
                        raise RuntimeError(f"Failed to create a game for {game_id}")
                    log_info(f"Client {client_addr} created the game {game_id}")
                    response["type"] = Response.CREATE_GAME.value
                    response["data"] = game.game_id
                    client_sock.send(json.dumps(response).encode("utf-8"))

                elif int(data["type"]) == Request.MAKE_THE_MOVE.value:
                    game_id = int(data['game_id'])
                    host_id = data['host_id']
                    row_from = int(data['row_from'])
                    col_from = int(data['col_from'])
                    row_to = int(data['row_to'])
                    col_to = int(data['col_to'])
                    username = data['username']
                    log_info(f"User {username} tried to make move in game {game_id}.")
                    if not self.check_connection(username, host_id):
                        raise ValueError("User is not connected")
                    if game_id < 0 or game_id >= self.games_count:
                        raise IndexError(f"Game ID {game_id} does not exist")
                    game = self.games[game_id]
                    if not any(client and client.host_id == host_id for client in game.clients):
                        raise ValueError(f"Invalid client ID {host_id} for game {game_id}")
                    player_mark = BLACK
                    if game.clients[0].host_id == host_id:
                        player_mark = WHITE
                    if game.board.current_player_color() != player_mark:
                        raise ValueError("Not your turn")
                    if not game.board.move_piece(row_from, col_from, row_to, col_to):
                        raise ValueError("Unable to make a move")
                    log_info(f"Player {host_id} made move to ({row_to}, {col_to}) in game {game_id}")
                    winner = game.check_winner()
                    if winner == WHITE or winner == BLACK:
                        game.finished = True
                        log_info(f"Game {game_id} finished. Winner: {winner}")
                        for client in game.clients:
                            if client.host_name == username:
                                database.update_stats(client.host_name, True)
                            else:
                                database.update_stats(client.host_name, False)
                            win_resp = {
                                "type": Response.GAME_FINISHED_SUC.value,
                                'row_from': row_from,
                                'col_from': col_from,
                                'row_to': row_to,
                                'col_to': col_to,
                            }
                            client.host_sock.send(json.dumps(win_resp).encode("utf-8"))
                        self.remove_game(game_id)
                    else:
                        for client in game.clients:
                            move_resp = {
                                "type": Response.MOVE_MADE.value,
                                'row_from': row_from,
                                'col_from': col_from,
                                'row_to': row_to,
                                'col_to': col_to,
                            }
                            client.host_sock.send(json.dumps(move_resp).encode("utf-8"))

            except (ConnectionResetError, BrokenPipeError):
                log_warn(f"Client {client_addr} disconnected.")
                if host_id is not None:
                    for game in self.games:
                        if game.finished:
                            continue
                        for i, client in enumerate(game.clients):
                            if client and client.host_addr == client_addr:
                                player_index = 1
                                if game.clients[0].host_id == host_id:
                                    player_index = 0
                                player_mark = BLACK if player_index == 1 else WHITE
                                winner = WHITE if player_mark == BLACK else BLACK
                                for client in game.clients:
                                    try:
                                        win_resp = {
                                            "type": Response.GAME_FINISHED_TECH.value,
                                        }
                                        client.host_sock.send(json.dumps(win_resp).encode("utf-8"))
                                    except Exception:
                                        log_error(f"Unable to notify client {client.host_id}.")
                                self.remove_game(game.game_id)
                break
            
            except Exception as e:
                log_error(f"Error handling client {client_addr}: {e}")
                traceback.print_exc()
                error_resp = {
                    "type": Response.ERROR.value,
                    "data": str(e)
                }
                client_sock.send(json.dumps(error_resp).encode("utf-8"))
        self.remove_connection(username, host_id)
        database.close()


def start_server():
    server_sock = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
    server_addr = (IP, PORT)
    server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_sock.bind(server_addr)
    server_sock.listen(16)
    server_sock.settimeout(5)
    server = GameServer(server_sock, server_addr)
    log_info(f"Server started on {server_addr[0]}:{server_addr[1]}")
    try:
        while True:
            try:
                client_sock, client_addr = server_sock.accept()
                thread = Thread(target=server.serve_connection, args=(client_sock, client_addr))
                thread.daemon = True
                thread.start()
            except TimeoutError:
                log_info("No clients requesting for connection.")
    except KeyboardInterrupt:
        log_info("Server shutdown requested by user.")
    finally:
        server.close_database()
        server_sock.close()
        log_info("Server socket closed.")


if __name__ == "__main__":
    start_server()