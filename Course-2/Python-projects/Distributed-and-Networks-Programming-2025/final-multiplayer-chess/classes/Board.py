from constants.Constants import WHITE, BLACK
from classes.Figures import Rook, King, Knight, Bishop, Queen, Pawn


# Chess board model
class Board:
    def __init__(self):
        # Current player color
        self.color = WHITE
        # Empty list for future figures_images
        self.field = [[None] * 8 for _ in range(8)]
        self.castling = True
        # Boolean if king is under attack
        self.alert = False
        self.initialize_field()

    # Check if coordinates are correct
    def correct_coords(self, row, col):
        return 0 <= row < 8 and 0 <= col < 8
    
    # Return opposite color
    def opponent(self, color):
        if color == WHITE:
            return BLACK
        else:
            return WHITE
        
    def is_not_under_attack(self, row1, col1, color):
        for row in range(8):
            for col in range(8):
                piece = self.get_piece(row, col)
                if piece and piece.get_color() != color:
                    if (piece.char() == 'P' and piece.can_attack(self, row, col, row1, col1, king_check=True)) or \
                            piece.can_attack(self, row, col, row1, col1):
                        return False
        return True

    def initialize_field(self):
        self.field[0][0] = Rook(WHITE)
        self.field[0][1] = Knight(WHITE)
        self.field[0][2] = Bishop(WHITE)
        self.field[0][3] = Queen(WHITE)
        self.field[0][4] = King(WHITE)
        self.field[0][5] = Bishop(WHITE)
        self.field[0][6] = Knight(WHITE)
        self.field[0][7] = Rook(WHITE)
        for i in range(0, 8):
            self.field[1][i] = Pawn(WHITE)

        self.field[7][0] = Rook(BLACK)
        self.field[7][1] = Knight(BLACK)
        self.field[7][2] = Bishop(BLACK)
        self.field[7][3] = Queen(BLACK)
        self.field[7][4] = King(BLACK)
        self.field[7][5] = Bishop(BLACK)
        self.field[7][6] = Knight(BLACK)
        self.field[7][7] = Rook(BLACK)
        for i in range(0, 8):
            self.field[6][i] = Pawn(BLACK)

    def is_checkmate(self, color=None):
        king_coords = None
        if color == None:
            king_coords = self.find_figure_coords('K', self.color)
        else:
            king_coords = self.find_figure_coords('K', color)
        king = self.get_piece(king_coords[0], king_coords[1])
        if not self.alert:
            return False
        for i in [-1, 0, 1]:
            for j in [-1, 0, 1]:
                if i == j == 0:
                    continue
                if king.can_move(self, king_coords[0], king_coords[1], king_coords[0] + i, king_coords[1] + j):
                    return False
        return True

    # Return player's color
    def current_player_color(self):
        return self.color

    def find_figure_coords(self, figure_type, color):
        for row in range(8):
            for col in range(8):
                figure = self.field[row][col]
                if figure is not None and figure.char() == figure_type and color == figure.get_color():
                    return [row, col]
        return None

    # Check if king is under attack
    def king_alert(self, coords=[]):
        if not coords:
            coords = self.find_figure_coords('K', self.color)

        if not self.is_not_under_attack(coords[0], coords[1], self.color):
            self.alert = True
            return True
        self.alert = False
        return False

    # Return info about a figure
    def cell(self, row, col):
        piece = self.field[row][col]
        if piece is None:
            return '  '
        color = piece.get_color()
        c = 'w' if color == WHITE else 'b'
        return c + piece.char()

    # Get figure by its coordinates
    def get_piece(self, row, col):
        if 0 <= row < 8 and 0 <= col < 8:
            return self.field[row][col]
        else:
            return None

    # Moves piece by given coordinates
    def move_piece(self, row, col, row1, col1):
        if not self.correct_coords(row, col) or not self.correct_coords(row1, col1) or (row == row1 and col == col1):
            return False

        piece = self.field[row][col]
        if piece is None or piece.color != self.color:
            return False
        target = self.field[row1][col1]
        if target is None:
            if not piece.can_move(self, row, col, row1, col1):
                return False
        elif target.color == self.opponent(piece.color):
            if not piece.can_attack(self, row, col, row1, col1):
                return False
        elif piece.char() == 'K' and target.char() == 'R':
            if self.castling_0(row, col, row1, col1) or self.castling_7(row, col, row1, col1):
                return True
        else:
            return False

        if self.alert:
            piece_2 = self.field[row1][col1]
            self.field[row][col] = None
            self.field[row1][col1] = piece
            if piece.char() == 'K':
                if self.king_alert([row1, col1]):
                    self.field[row][col] = piece
                    self.field[row1][col1] = piece_2
                    return False
            else:
                if self.king_alert():
                    self.field[row][col] = piece
                    self.field[row1][col1] = piece_2
                    return False
        self.field[row][col] = None
        self.field[row1][col1] = piece
        if piece.char() == 'P' and ((piece.get_color() == WHITE and row1 == 7) or (piece.get_color() == BLACK and row1 == 0)):
            self.promote_pawn('Q', [row1, col1])
        self.color = self.opponent(self.color)
        self.king_alert()
        return True

    # Long castling availability check
    def castling_0(self, row, col, row1, col1):
        king = self.get_piece(row, col)
        rook = self.get_piece(row1, col1)
        if king is None or rook is None or king.char() != 'K' or rook.char() != 'R' or \
                not self.castling or king.color != rook.color:
            return False
        for i in (1, 2, 3):
            if self.field[row][col - i] is not None:
                return False

        if not self.is_not_under_attack(row1, col1, rook.color):
            return False
        for i in range(1, 4):
            if not self.is_not_under_attack(row, col - i, king.color):
                return False

        piece = self.field[row][col]
        self.field[row][col] = None
        self.field[row][col - 2] = piece
        piece = self.field[row1][col1]
        self.field[row1][col1] = None
        self.field[row1][col1 + 3] = piece
        self.color = self.opponent(self.color)
        self.castling = False
        return True

    # Short castling availability check
    def castling_7(self, row, col, row1, col1):
        king = self.field[row][col]
        rook = self.field[row1][col1]
        if king is None or rook is None or king.char() != 'K' or rook.char() != 'R' \
                or not self.castling or king.color != rook.color:
            return False
        for i in (1, 2):
            if self.field[row][col + i] is not None:
                return False
        if not self.is_not_under_attack(row1, col1, rook.color):
            return False
        if not self.is_not_under_attack(row, col + 1, king.color):
            return False
        if not self.is_not_under_attack(row, col + 2, king.color):
            return False
        piece = self.field[row][col]
        self.field[row][col] = None
        self.field[row][col + 2] = piece
        piece = self.field[row1][col1]
        self.field[row1][col1] = None
        self.field[row1][col1 - 2] = piece
        self.color = self.opponent(self.color)
        self.castling = False
        return True

    # Pawn promotion
    def promote_pawn(self, char, pawn_coords):
        color = self.field[pawn_coords[0]][pawn_coords[1]].get_color()
        if char == 'Q':
            self.field[pawn_coords[0]][pawn_coords[1]] = Queen(color)
        elif char == 'R':
            self.field[pawn_coords[0]][pawn_coords[1]] = Rook(color)
        elif char == 'B':
            self.field[pawn_coords[0]][pawn_coords[1]] = Bishop(color)
        elif char == 'N':
            self.field[pawn_coords[0]][pawn_coords[1]] = Knight(color)
        return True
