from enum import Enum

# Specifies types of requests
class Request(Enum):
    GET_GAMES = 1
    CREATE_THE_GAME = 2
    CONNECT_TO_GAME = 3
    MAKE_THE_MOVE = 4
    REMOVE_THE_GAME = 5 
    AUTHENTICATE = 6
    CREATE_USER = 7
    GET_USER_DATA = 8

# Specifies types of responses
class Response(Enum):
    RETURN_GAMES = 1
    CREATE_GAME = 2
    CONNECT_TO_GAME = 3
    START_GAME = 4
    GAME_FINISHED_SUC = 5
    GAME_FINISHED_TECH = 6
    MOVE_MADE = 7
    ERROR = 8
    PLAYER_DISCONNECTED = 9
    AUTHORIZED = 10
    UNAUTHORIZED = 11
    USER_DATA = 12