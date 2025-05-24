import pygame
import pygame_menu
import os
from constants.Constants import FONT_SIZE, SESSIONS_PER_PAGE, WIDTH, HEIGHT, BLACK, WHITE, FIGURES_PNG, CELL_SIZE, BACKGROUND_COLOR
from classes.Renderer import Renderer
from classes.Board import Board
from log_meth.log import log_error, log_info, log_warn
from constants.Protocols import Request, Response
import socket
import json
from time import sleep

ACTION_REGISTER = 0
ACTION_LOGIN = 1

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.settimeout(5)
BUFFER_SIZE = 8192
SERVER_ADDRESS = ("127.0.0.1", 8080)
pygame.init()
pygame.display.set_caption("Chess")
screen = pygame.display.set_mode((WIDTH, HEIGHT))
clock = pygame.time.Clock()
clock.tick(30)
host_id = None
username = None
password = None


def send_request(request_type, data=None):
    global host_id, username
    try:
        request = {"type": request_type.value}
        if host_id is not None:
            request['username'] = username
            request['host_id'] = host_id
        if data:
            request.update(data)
        client_socket.send(json.dumps(request).encode('utf-8'))
        response = json.loads(client_socket.recv(BUFFER_SIZE).decode('utf-8'))
        return response
    except Exception as e:
        log_error(f"Network error: {e}")
        return None

def create_enter_menu(result, set_username, set_password, set_action, try_submit):
    menu = pygame_menu.Menu('Welcome', WIDTH, HEIGHT,theme=pygame_menu.themes.THEME_SOLARIZED)
    menu.add.label("Enter your details", font_size=40, font_color=(0, 0, 0))
    menu.add.vertical_margin(10)
    menu.add.text_input('Login: ', onchange=set_username)
    menu.add.text_input('Password: ', password=True, onchange=set_password)
    menu.add.selector('Mode: ', [('Registration', 0), ('Enter', 1)], onchange=set_action)
    error_label = menu.add.label('', font_color=(255, 0, 0))
    menu.add.vertical_margin(20)
    menu.add.button('Continue', try_submit, background_color=(0, 183, 235), font_color=(255, 255, 255))
    menu.add.vertical_margin(10)
    menu.add.button('Exit', pygame_menu.events.EXIT, background_color=(255, 0, 0), font_color=(255, 255, 255))
    return menu, error_label


def get_user_credentials_gui(send_callback):
    result = {
        "username": None,
        "password": None,
        "action": ACTION_REGISTER,
        "submitted": False,
        "error": ""
    }

    def try_submit():
        if not result["username"] or not result["password"]:
            result["error"] = "Please enter login and password"
            return

        request_type = Request.CREATE_USER if result["action"] == ACTION_REGISTER else Request.AUTHENTICATE
        response = send_callback(request_type, {
            "username": result["username"],
            "password": result["password"]
        })

        if response["type"] == Response.AUTHORIZED.value:
            result["submitted"] = True
            result["host_id"] = response["host_id"]
        elif response["type"] == Response.ERROR.value:
            result["error"] = response["data"]
        else:
            result["error"] = "Authorization error"
    
    menu, error_label = create_enter_menu(
        result,
        set_username=lambda v: result.update({"username": v}),
        set_password=lambda v: result.update({"password": v}),
        set_action=lambda _, v: result.update({"action": v}),
        try_submit=try_submit
    )

    while not result["submitted"]:
        events = pygame.event.get()
        for event in events:
            if event.type == pygame.QUIT:
                pygame.quit()
                exit()

        screen.fill((240, 240, 240)) 
        error_label.set_title(result["error"])
        menu.update(events)
        menu.draw(screen)
        pygame.display.flip()

    return result["username"], result["password"], result["action"], result["host_id"]


def connect_to_server():
    global host_id, username, password

    while host_id is None:
        try:
            client_socket.connect(SERVER_ADDRESS)
        except Exception as e:
            log_error(f"Failed to connect to server: {e}")
            sleep(5)
        else:
            while True:
                username, password, action, host_id = get_user_credentials_gui(send_request)
                if host_id is not None:
                    break
                else:
                    log_error("Authorization error. Try again.")



def load_images():
    colors = ['b', 'w']
    figures = ['B', 'K', 'N', 'P', 'Q', 'R']
    for c in colors:
        for f in figures:
            path = os.path.join('figures_images', f'{c}{f}.png')
            FIGURES_PNG[f'{c}{f}'] = pygame.image.load(path).convert_alpha()
            FIGURES_PNG[f'{c}{f}'] = pygame.transform.scale(FIGURES_PNG[f'{c}{f}'], [CELL_SIZE, CELL_SIZE])


def start_game(game_id=None, is_host=False):
    global FONT, screen
    player_mark = None
 
    if game_id is not None:
        if is_host:
            response = send_request(Request.CREATE_THE_GAME)
            if response and response.get("type") == Response.CREATE_GAME.value:
                game_id = response["data"]
                player_mark = WHITE
                if show_waiting_screen(game_id):
                    return
            else:
                log_error("Failed to create game.")
                return
        else:
            response = send_request(Request.CONNECT_TO_GAME, {"game_id": game_id})
            if response and response.get("type") == Response.CONNECT_TO_GAME.value:
                player_mark = BLACK
            else:
                log_error("Failed to join game.")
                return
            
    FONT = pygame.font.Font(None, FONT_SIZE)
    renderer, board = initialize_screen(player_mark)
    renderer.render(board)

    client_socket.setblocking(0)
    end = False
    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            if not end and event.type == pygame.MOUSEBUTTONDOWN and player_mark == board.current_player_color():
                cell_data = renderer.get_cell_by_click(board)
                if not cell_data or not renderer.chosen_coords or cell_data == renderer.chosen_coords:
                    continue
                figure, x1, y1 = renderer.chosen_coords
                dest, x2, y2 = cell_data
                if board.move_piece(x1, y1, x2, y2):
                    response = send_request(Request.MAKE_THE_MOVE, {
                        "game_id": game_id,
                        "row_from": x1,
                        "col_from": y1,
                        "row_to" : x2,
                        "col_to" : y2
                    })
                    if response and (response.get("type") != Response.MOVE_MADE.value and response.get("type") != Response.GAME_FINISHED_SUC.value):
                        end = True
                renderer.empty_chosen_coords()
                if board.is_checkmate():
                    end = True

        if game_id is not None and not end:
            try:
                data = client_socket.recv(BUFFER_SIZE)
                if data:
                    response = json.loads(data.decode('utf-8'))
                    if response["type"] == Response.MOVE_MADE.value:
                        row_from = int(response['row_from'])
                        col_from = int(response['col_from'])
                        row_to = int(response['row_to'])
                        col_to = int(response['col_to'])
                        board.move_piece(row_from, col_from, row_to, col_to)
                    elif response["type"] == Response.GAME_FINISHED_SUC.value:
                        row_from = int(response['row_from'])
                        col_from = int(response['col_from'])
                        row_to = int(response['row_to'])
                        col_to = int(response['col_to'])
                        board.move_piece(row_from, col_from, row_to, col_to)
                        end = True
                    elif response["type"] == Response.GAME_FINISHED_TECH.value:
                        end = True
                        log_info("Opponent has disconnected.")
                        renderer.finish_game()
            except BlockingIOError:
                pass
            except TimeoutError:
                log_info("Waiting for another host to make a move.")

        renderer.render(board)
        pygame.display.flip()

    if not end:
        send_request(Request.REMOVE_THE_GAME, {"game_id": game_id})
    client_socket.setblocking(1)


# Waiting screen
def show_waiting_screen(game_id):
    waiting = True
    clock = pygame.time.Clock()
    font = pygame.font.Font(None, 36)

    screen.fill(BACKGROUND_COLOR)
    text = font.render("Waiting for opponent...", True, (0, 0, 0))
    text_rect = text.get_rect(center=(WIDTH / 2, HEIGHT / 2))
    screen.blit(text, text_rect)
    pygame.display.flip()

    interrupted = False

    client_socket.setblocking(0)
    player_joined = False
    while waiting:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                interrupted = True
                waiting = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    waiting = False

        try:
            data = client_socket.recv(BUFFER_SIZE)
            if data:
                response = json.loads(data.decode('utf-8'))
                if response["type"] == Response.START_GAME.value:
                    waiting = False
                    player_joined = True
        except BlockingIOError:
            pass
        except KeyboardInterrupt:
            interrupted = True
            waiting = False
        except TimeoutError:
            log_info("Waiting for game to start...")

        screen.fill(BACKGROUND_COLOR)
        text = font.render("Waiting for opponent...", True, (0, 0, 0))
        text_rect = text.get_rect(center=(WIDTH / 2, HEIGHT / 2))
        screen.blit(text, text_rect)

        pygame.display.flip()
        clock.tick(30)
    if not player_joined:
        send_request(Request.REMOVE_THE_GAME, {"game_id": game_id})
    client_socket.setblocking(1)
    return interrupted


# Shows lobby to player
def lobby(page=0):
    current_page = page
    client_socket.setblocking(1)
    log_info("Lobby was opened.")
    try:
        response = send_request(Request.GET_GAMES, {
            "page_num_l": page * SESSIONS_PER_PAGE,
            "page_num_r": (page + 1) * SESSIONS_PER_PAGE - 1,
        })
    except Exception:
        connect_to_server()
 
    if not response or response.get("type") != Response.RETURN_GAMES.value:
        log_error("Failed to get game list")
        return
 
    available_games = response["data"]
    total_pages = max((len(available_games) + SESSIONS_PER_PAGE - 1) // SESSIONS_PER_PAGE, 1)
    total_sessions = len(available_games)

    lobby_menu = pygame_menu.Menu("Lobby", WIDTH, HEIGHT, theme=pygame_menu.themes.THEME_SOLARIZED)

    controls_frame = lobby_menu.add.frame_h(width=WIDTH * 0.8, height=50)

    page_info_label = controls_frame.pack(
        lobby_menu.add.label(f"Page {current_page + 1}/{total_pages}", font_size=20),
        align=pygame_menu.locals.ALIGN_CENTER
    )

    content_frame = lobby_menu.add.frame_v(
        width=WIDTH * 0.8,
        height=HEIGHT * 0.7,
        background_color='grey'
    )

    start_idx = current_page * SESSIONS_PER_PAGE
    end_idx = min(start_idx + SESSIONS_PER_PAGE, total_sessions)

    for game_id in range(start_idx, end_idx):
        content_frame.pack(
            lobby_menu.add.button(
                f"Game {available_games[game_id][0] + 1} | Host {available_games[game_id][1]}",
                lambda g=game_id: start_game(g),
                font_size=20
            ),
            align=pygame_menu.locals.ALIGN_CENTER
        )

    def change_page(delta):
        nonlocal current_page
        new_page = current_page + delta

        if 0 <= new_page < total_pages:
            lobby_menu.close()
            lobby(new_page)

    btn_prev = controls_frame.pack(
        lobby_menu.add.button(
            "<",
            lambda: change_page(-1),
            font_size=20
        ),
        align=pygame_menu.locals.ALIGN_LEFT
    )

    btn_next = controls_frame.pack(
        lobby_menu.add.button(
            ">",
            lambda: change_page(1),
            font_size=20
        ),
        align=pygame_menu.locals.ALIGN_RIGHT
    )

    lobby_menu.add.button("Create Game", lambda: start_game(game_id=-1, is_host=True), font_size=20)
    lobby_menu.add.button("Refresh", lambda: lobby(current_page), font_size=20)
    lobby_menu.add.button("Back", lambda: menu(), font_size=20)

    lobby_menu.mainloop(screen)

def watch_statistics():
    try:
        response = send_request(Request.GET_USER_DATA)
        rep_type = response['type']
        rep_wins = response['wins']
        rep_losses =  response['losses']
        total_macthes = response['total_matches']
        if total_macthes != 0:
            rate = rep_wins / total_macthes
        else:
            rate = 0

        stats_menu = pygame_menu.Menu('Statistics', WIDTH, HEIGHT, theme=pygame_menu.themes.THEME_SOLARIZED)

        stats_menu.add.label(f"Wins: {rep_wins}")
        stats_menu.add.label(f"Losses: {rep_losses}")
        stats_menu.add.label(f"Total Matches: {total_macthes}")
        stats_menu.add.label(f"Rate: {rate * 100}%")

        def back_to_main():
            stats_menu.disable()
            menu.enable() 
        stats_menu.add.button('Back', back_to_main)
        stats_menu.mainloop(screen)

    except Exception as e:
        log_error(str(e))


def initialize_screen(spectator):
    renderer = Renderer(screen, spectator)
    board = Board()
    renderer.render(board)
    return renderer, board

def menu():
    menu = pygame_menu.Menu("Chess", WIDTH, HEIGHT, theme=pygame_menu.themes.THEME_SOLARIZED)
    menu.add.button('Play', lobby)
    menu.add.button('Statistics', watch_statistics)
    menu.add.button('Quit', pygame_menu.events.EXIT)
    menu.mainloop(screen)


if __name__ == '__main__':
    # Should be called insted of menu and should me modified to have UI
    connect_to_server()
    load_images()
    menu()