from constants.Constants import WHITE, BLACK
import copy


# Model of chess figure
class Figure:
    def __init__(self, color, character):
        self.color = color
        self.character = character

    def get_color(self):
        return self.color

    def char(self):
        return self.character

    def can_move(self, board, row, col, row1, col1, king_check=False):
        return True

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        return True


# Rook model
class Rook(Figure):
    def __init__(self, color):
        super().__init__(color, 'R')

    def get_color(self):
        return self.color

    def char(self):
        return self.character

    def can_move(self, board, row, col, row1, col1, king_check=False):
        if not board.correct_coords(row1, col1):
            return False
        piece1 = board.get_piece(row1, col1)
        if not (piece1 is None) and piece1.get_color() == self.color:
            return False
        if row == row1 or col == col1:
            step = 1 if (row1 >= row) else -1
            for i in range(row + step, row1, step):
                if not (board.get_piece(i, col) is None):
                    return False
            step = 1 if (col1 >= col) else -1
            for i in range(col + step, col1, step):
                if not (board.get_piece(row, i) is None):
                    return False
            return True
        return False

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        return self.can_move(board, row, col, row1, col1)


# Pawn model
class Pawn(Figure):
    def __init__(self, color):
        super().__init__(color, 'P')

    def get_color(self):
        return self.color

    def char(self):
        return self.character

    def can_move(self, board, row, col, row1, col1, king_check=False):
        if not board.correct_coords(row1, col1):
            return False
        if self.color == WHITE:
            start_row = 1
        else:
            start_row = 6
        if (row1 - row == 2 and self.color == WHITE) or (row1 - row == -2 and self.color == BLACK):
            if row != start_row:
                return False
            if col != col1:
                return False
            if board.get_piece(row1, col1) is not None:
                return False
            return True
        if (row1 - row == 1 and self.color == WHITE) or (row1 - row == -1 and self.color == BLACK):
            if col1 != col:
                if abs(col1 - col) != 1:
                    return False
                if board.get_piece(row1, col1) is None:
                    if king_check:
                        return True
                    else:
                        return False
                if board.get_piece(row1, col1).get_color() == self.color:
                    return False
                else:
                    return True
            else:
                if board.get_piece(row1, col1) is not None:
                    return False
                return True
        return False

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        if not board.correct_coords(row1, col1):
            return False
        if (row1 - row == 2 and self.color == WHITE) or (row1 - row == -2 and self.color == BLACK):
            return False
        if (row1 - row == 1 and self.color == WHITE) or (row1 - row == -1 and self.color == BLACK):
            if col1 != col:
                if abs(col1 - col) != 1:
                    return False
                if board.get_piece(row1, col1) is None:
                    if king_check:
                        return True
                    else:
                        return False
                if board.get_piece(row1, col1).get_color() == self.color:
                    return False
                else:
                    return True
            else:
                return False
        return False


# Knight model
class Knight(Figure):
    def __init__(self, color):
        super().__init__(color, 'N')

    def get_color(self):
        return self.color

    def char(self):
        return self.character

    def can_move(self, board, row, col, row1, col1, king_check=False):
        if not board.correct_coords(row1, col1):
            return False
        piece1 = board.get_piece(row1, col1)
        if not (piece1 is None) and piece1.get_color() == self.color:
            return False
        if str(abs(row1 - row)) + str(abs(col1 - col)) in ('12', '21'):
            return True
        return False

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        return self.can_move(board, row, col, row1, col1)


# Queen model
class Queen(Figure):
    def __init__(self, color):
        super().__init__(color, 'Q')
        self.color = color

    def get_color(self):
        return self.color

    def char(self):
        return 'Q'

    def can_move(self, board, row, col, row1, col1, king_check=False):
        if not board.correct_coords(row1, col1):
            return False
        piece1 = board.get_piece(row1, col1)
        if not (piece1 is None) and piece1.get_color() == self.color:
            return False
        if row == row1 or col == col1:
            step = 1 if (row1 >= row) else -1
            for i in range(row + step, row1, step):
                if not (board.get_piece(i, col) is None):
                    return False
            step = 1 if (col1 >= col) else -1
            for i in range(col + step, col1, step):
                if not (board.get_piece(row, i) is None):
                    return False
            return True
        if row - col == row1 - col1:
            step = 1 if (row1 >= row) else -1
            for i in range(row + step, row1, step):
                c = col - row + i
                if not (board.get_piece(i, c) is None):
                    return False
            return True
        if row + col == row1 + col1:
            step = 1 if (row1 >= row) else -1
            for i in range(row + step, row1, step):
                c = row + col - i
                if not (board.get_piece(i, c) is None):
                    return False
            return True
        return False

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        return self.can_move(board, row, col, row1, col1)


# Bishop model
class Bishop(Figure):
    def __init__(self, color):
        super().__init__(color, 'B')

    def get_color(self):
        return self.color

    def char(self):
        return 'B'

    def can_move(self, board, row, col, row1, col1, king_check=False):
        if not board.correct_coords(row1, col1):
            return False
        piece1 = board.get_piece(row1, col1)
        if not (piece1 is None) and piece1.get_color() == self.color:
            return False
        if row - col == row1 - col1:
            step = 1 if (row1 >= row) else -1
            for i in range(row + step, row1, step):
                c = col - row + i
                if not (board.get_piece(i, c) is None):
                    return False
            return True
        if row + col == row1 + col1:
            step = 1 if (row1 >= row) else -1
            for i in range(row + step, row1, step):
                c = row + col - i
                if not (board.get_piece(i, c) is None):
                    return False
            return True
        return False

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        return self.can_move(board, row, col, row1, col1)


# King model
class King(Figure):
    def __init__(self, color):
        super().__init__(color, 'K')

    def get_color(self):
        return self.color

    def char(self):
        return 'K'

    def can_move(self, board, row, col, row1, col1, king_check=False):
        try:
            if not board.correct_coords(row1, col1):
                return False
            piece1 = board.get_piece(row1, col1)
            if not (piece1 is None) and piece1.get_color() == self.color:
                return False
            if str(abs(row1 - row)) + str(abs(col1 - col)) not in ('01', '10', '11'):
                return False
            temp = copy.deepcopy(piece1)
            board.field[row1][col1] = None
            if not board.is_not_under_attack(row1, col1, self.color):
                board.field[row1][col1] = temp
                return False
            board.field[row1][col1] = temp
            return True
        except RecursionError:
            return False

    def can_attack(self, board, row, col, row1, col1, king_check=False):
        return self.can_move(board, row, col, row1, col1)
