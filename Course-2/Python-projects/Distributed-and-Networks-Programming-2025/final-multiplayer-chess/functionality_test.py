import pytest
from classes.Board import Board
from classes.Figures import Rook, King, Knight, Bishop, Queen, Pawn
from constants.Constants import WHITE, BLACK


def test_board_function():
    test_board = Board()
    answer = [[None] * 8 for _ in range(8)]
    answer[0][0] = Rook(WHITE)
    answer[0][1] = Knight(WHITE)
    answer[0][2] = Bishop(WHITE)
    answer[0][3] = Queen(WHITE)
    answer[0][4] = King(WHITE)
    answer[0][5] = Bishop(WHITE)
    answer[0][6] = Knight(WHITE)
    answer[0][7] = Rook(WHITE)
    for i in range(0, 8):
        answer[1][i] = Pawn(WHITE)

    answer[7][0] = Rook(BLACK)
    answer[7][1] = Knight(BLACK)
    answer[7][2] = Bishop(BLACK)
    answer[7][3] = Queen(BLACK)
    answer[7][4] = King(BLACK)
    answer[7][5] = Bishop(BLACK)
    answer[7][6] = Knight(BLACK)
    answer[7][7] = Rook(WHITE)
    for i in range(0, 8):
        answer[6][i] = Pawn(BLACK)
    assert board_equality(answer, test_board.field)


def board_equality(board1, board2):
    try:
        for i in range(8):
            for j in range(8):
                if not (board1[i][j] == board2[i][j] is None or board1[i][j].char() == board2[i][j].char()):
                    return False
        return True
    except AttributeError:
        return False


def move_validation(figure_coord, coord1, coord2, is_valid=False, clear_pawn=None):
    try:
        test_board = Board()
        char = test_board.get_piece(figure_coord[0], figure_coord[1]).char()
        if clear_pawn:
            test_board.field[clear_pawn[0]][clear_pawn[1]] = None
        # valid one
        moved1 = test_board.move_piece(figure_coord[0], figure_coord[1], coord1[0], coord1[1])
        test_board.color = WHITE
        # may be valid/invalid
        moved2 = test_board.move_piece(coord1[0], coord1[1], coord2[0], coord2[1])
        if is_valid:
            no_figures = not test_board.get_piece(figure_coord[0], figure_coord[1]) and not test_board.get_piece(coord1[0], coord1[1])
            figure_on_place = test_board.get_piece(coord2[0], coord2[1]).char() == char
            return moved1 and moved2 and no_figures and figure_on_place
        else:
            no_figure = not test_board.get_piece(figure_coord[0], figure_coord[1])
            figure_on_place = test_board.get_piece(coord1[0], coord1[1]).char() == char
            return moved1 and not moved2 and no_figure and figure_on_place
    except AttributeError:
        return False


def test_valid_knight():
    figure_coord = [0, 1]
    coord1 = [2, 0]
    coord2 = [3, 2]
    assert move_validation(figure_coord, coord1, coord2, True)


def test_invalid_knight():
    figure_coord = [0, 1]
    coord1 = [2, 0]
    coord2 = [3, 3]
    assert move_validation(figure_coord, coord1, coord2, False)


def test_valid_rook():
    figure_coord = [0, 0]
    clear_pawn = [1, 0]
    coord1 = [5, 0]
    coord2 = [5, 5]
    assert move_validation(figure_coord, coord1, coord2, True, clear_pawn)


def test_invalid_rook():
    figure_coord = [0, 0]
    clear_pawn = [1, 0]
    coord1 = [5, 0]
    coord2 = [4, 4]
    assert move_validation(figure_coord, coord1, coord2, False, clear_pawn)


def test_valid_bishop():
    figure_coord = [0, 2]
    clear_pawn = [1, 1]
    coord1 = [2, 0]
    coord2 = [4, 2]
    assert move_validation(figure_coord, coord1, coord2, True, clear_pawn)


def test_invalid_bishop():
    figure_coord = [0, 2]
    clear_pawn = [1, 3]
    coord1 = [2, 4]
    coord2 = [7, 7]
    assert move_validation(figure_coord, coord1, coord2, False, clear_pawn)


def test_valid_queen():
    figure_coord = [0, 3]
    clear_pawn = [1, 2]
    coord1 = [2, 1]
    coord2 = [2, 4]
    assert move_validation(figure_coord, coord1, coord2, True, clear_pawn)


def test_invalid_queen():
    figure_coord = [0, 3]
    clear_pawn = [1, 3]
    coord1 = [3, 3]
    coord2 = [5, 7]
    assert move_validation(figure_coord, coord1, coord2, False, clear_pawn)


def test_valid_king():
    figure_coord = [0, 4]
    clear_pawn = [1, 4]
    coord1 = [1, 4]
    coord2 = [2, 5]
    assert move_validation(figure_coord, coord1, coord2, True, clear_pawn)


def test_invalid_king():
    figure_coord = [0, 4]
    clear_pawn = [1, 4]
    coord1 = [1, 4]
    coord2 = [3, 1]
    assert move_validation(figure_coord, coord1, coord2, False, clear_pawn)


def test_valid_pawn():
    figure_coord = [1, 0]
    coord1 = [3, 0]
    coord2 = [4, 0]
    assert move_validation(figure_coord, coord1, coord2, True)


def test_invalid_pawn():
    figure_coord = [1, 5]
    coord1 = [2, 5]
    coord2 = [0, 5]
    assert move_validation(figure_coord, coord1, coord2, False)
