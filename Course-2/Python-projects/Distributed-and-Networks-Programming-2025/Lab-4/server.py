import grpc
from concurrent import futures
import time
import game_pb2 as cf
import game_pb2_grpc as cf_grpc


class ConnectFourServicer(cf_grpc.ConnectFourServicer):
    def __init__(self):
        self.games = []

    def CreateGame(self, request, context):
        print(f"Request for Game creation was received.")
        gameId = self.nextGameId
        game = cf.Game(
            id=gameId,
            turn=cf.MARK_RED,
            isFinished=False
        )
        self.games.append(game)
        print(f"Game {gameId} was created.")
        return game

    def GetGame(self, request, context):
        print(f"Request for Game ID {request.gameId}")
        game = self.game[request.gameId]
        if not game:
            print(f"Game {request.gameId} was not found.")
            context.abort(grpc.StatusCode.NOT_FOUND, "game not found.")
        print(f"Game {request.gameId} was returned.")
        return game

    def MakeMove(self, request, context):
        gameId = request.gameId
        move = request.move
        col = move.column
        mark = move.mark
        print(f"Move request was received: Game {gameId}, Player {mark}, Column {col}")
        game = self.games[gameId]
        if not game:
            print(f"Game {gameId} was not found.")
            context.abort(grpc.StatusCode.NOT_FOUND, "game not found.")
        if game.isFinished:
            print(f"Game {gameId} has already been finished.")
            context.abort(grpc.StatusCode.FAILED_PRECONDITION, "game is already finished.")
        if mark != game.turn:
            print(f"Not {mark}'s turn in Game {gameId}.")
            context.abort(grpc.StatusCode.FAILED_PRECONDITION, "not player's turn.")
        if not (1 <= col <= 7):
            print(f"Invalid column {col} in Game {gameId}")
            context.abort(grpc.StatusCode.INVALID_ARGUMENT, "invalid column.")
        moves = [m for m in game.moves if m.column == col]
        if len(moves) >= 6:
            print(f"Column {col} is full for Game {gameId}")
            context.abort(grpc.StatusCode.FAILED_PRECONDITION, "column is full.")
        game.moves.append(cf.Move(mark=mark, column=col))
        if self.isWin(game.moves, mark):
            game.winner = mark
            game.isFinished = True
            print(f"Player {mark} wins Game {gameId}.")
        elif self.isDraw(game.moves):
            game.isFinished = True
            print(f"Game {gameId} is a draw.")
        else:
            game.turn = cf.MARK_RED if mark == cf.MARK_YELLOW else cf.MARK_YELLOW
            print(f"Move was made in Game {gameId}: Player {mark}, Column {col}.")
        return game

    def isWin(self, moves, mark):
        board = [[None for _ in range(7)] for _ in range(6)]
        for move in moves:
            for row in range(5, -1, -1):
                if board[row][move.column - 1] is None:
                    board[row][move.column - 1] = move.mark
                    break
        for row in range(6):
            for col in range(7):
                if board[row][col] != mark:
                    continue
                if col <= 3 and all(board[row][col + i] == mark for i in range(4)):
                    return True
                if row <= 2 and all(board[row + i][col] == mark for i in range(4)):
                    return True
                if row <= 2 and col <= 3 and all(board[row + i][col + i] == mark for i in range(4)):
                    return True
                if row <= 2 and col >= 3 and all(board[row + i][col - i] == mark for i in range(4)):
                    return True
        return False

    def isDraw(self, moves):
        colCnt = {col: 0 for col in range(1, 8)}
        for move in moves:
            colCnt[move.column] += 1
        for col in range(1, 8):
            if colCnt[col] < 6:
                return False
        return True


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    cf_grpc.add_ConnectFourServicer_to_server(ConnectFourServicer(), server)
    server.add_insecure_port("0.0.0.0:50051")
    server.start()
    print("Server listens on the port: 50051")
    try:
        while True:
            time.sleep(86400)
    except KeyboardInterrupt:
        print("Shutting down.")
        server.stop(0)


if __name__ == "__main__":
    serve()
