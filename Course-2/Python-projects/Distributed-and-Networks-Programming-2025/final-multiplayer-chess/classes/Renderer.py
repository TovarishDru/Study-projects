import pygame
from constants.Constants import START_POS, START_POS_BLACK, START_POS_WHITE, CELL_SIZE, WHITE, BLACK
from constants.Constants import BACKGROUND_COLOR, WIDTH, HEIGHT, WHITE_CELL_COLOR, BLACK_CELL_COLOR, FIGURES_PNG


# Class to draw game on a client side
class Renderer:
    def __init__(self, screen, spectator=WHITE):
        self.screen = screen
        pygame.draw.rect(screen, BACKGROUND_COLOR, (0, 0, WIDTH, HEIGHT))
        # Who is sitting in front of the SCREEN?
        self.spectator = spectator
        # Check if figure marked as chosen on SCREEN
        self.chosen_coords = []
        self.checkmate = False
        self.game_finished = False

    def set_checkmate(self):
        self.checkmate = True

    # Unmark the figure after the move
    def empty_chosen_coords(self):
        self.chosen_coords = []

    def get_cell_by_click(self, board):
        x_mouse, y_mouse = pygame.mouse.get_pos()
        for row in range(8):
            for col in range(8):
                if START_POS[0] + col * CELL_SIZE < x_mouse < START_POS[0] + (col + 1) * CELL_SIZE and \
                        START_POS[1] + CELL_SIZE - (row + 1) * CELL_SIZE < y_mouse < START_POS[1] + CELL_SIZE - row * CELL_SIZE:
                    _row = row
                    if self.spectator == BLACK:
                        _row = 7-row
                    if board.get_piece(_row, col) and not self.chosen_coords and board.get_piece(_row, col).get_color() == board.color:
                        self.chosen_coords = [board.get_piece(_row, col), _row, col]
                    return [board.get_piece(_row, col), _row, col]
        self.chosen_coords = []
        return None
    
    def finish_game(self):
        self.game_finished = True

    def render(self, board):
        self.checkmate = board.is_checkmate()
        board.king_alert()
        # Draw board with figures_images
        for row in range(8):
            for col in range(8):
                cell_color = WHITE_CELL_COLOR
                if row % 2 == col % 2:
                    cell_color = BLACK_CELL_COLOR

                cell_rect = ()
                if self.spectator == WHITE:
                    cell_rect = (START_POS_WHITE[0] + CELL_SIZE * row, START_POS_WHITE[1] - CELL_SIZE * col,
                                 CELL_SIZE, CELL_SIZE)
                elif self.spectator == BLACK:
                    cell_rect = (START_POS_BLACK[0] + CELL_SIZE * row, START_POS_BLACK[1] + CELL_SIZE * col,
                                 CELL_SIZE, CELL_SIZE)

                pygame.draw.rect(self.screen, cell_color, cell_rect)
                if board.get_piece(col, row) is not None:
                    color = 'b'
                    if board.get_piece(col, row).get_color() == WHITE:
                        color = 'w'
                    img = FIGURES_PNG[f'{color}{board.get_piece(col, row).char()}']
                    # Mark figure if chosen
                    if self.chosen_coords and [col, row] == [self.chosen_coords[1], self.chosen_coords[2]]:
                        marker = pygame.Surface(img.get_size())
                        marker.fill((0, 255, 0, 10))
                        marker.blit(img, (0, 0), None)
                        img = marker
                    self.screen.blit(img, [cell_rect[0], cell_rect[1]])

        # Print letters and digits near the board
        for i in range(8):
            font = pygame.font.Font(None, 32)
            if self.spectator == WHITE:
                letter = font.render(f"{chr(ord('a') + i)}", False, 'black')
                digit = font.render(f'{i + 1}', False, 'black')
            elif self.spectator == BLACK:
                letter = font.render(f"{chr(ord('h') - i)}", False, 'black')
                digit = font.render(f'{8 - i}', False, 'black')

            self.screen.blit(letter,
                        [START_POS[0] + CELL_SIZE * i + CELL_SIZE * 0.4,
                         START_POS[1] + CELL_SIZE * 1.1])
            self.screen.blit(digit,
                        [START_POS[0] - CELL_SIZE * 0.3,
                         START_POS[1] + CELL_SIZE * 0.4 - CELL_SIZE * i])
        
        pygame.draw.rect(self.screen, BACKGROUND_COLOR, (0, START_POS[1] + 1.5 * CELL_SIZE, WIDTH, 100))
        if self.game_finished:
            font = pygame.font.Font(None, 60)
            check = font.render('Opponent quit!', False, 'black')
            self.screen.blit(check, [START_POS[0] + 2 * CELL_SIZE, START_POS[1] + 1.5 * CELL_SIZE])
        elif self.checkmate:
            font = pygame.font.Font(None, 64)
            check = font.render('CHECKMATE!', False, 'black')
            self.screen.blit(check, [START_POS[0] + 2 * CELL_SIZE, START_POS[1] + 1.5 * CELL_SIZE])
        elif board.alert and board.current_player_color() == self.spectator:
            font = pygame.font.Font(None, 64)
            check = font.render('CHECK!', False, 'black')
            self.screen.blit(check, [START_POS[0] + 3 * CELL_SIZE, START_POS[1] + 1.5 * CELL_SIZE])
        elif board.color != self.spectator and not self.checkmate and not self.game_finished:
            font = pygame.font.Font(None, 32)
            check = font.render("Waiting for opponnent's move...", False, 'black')
            self.screen.blit(check, [START_POS[0] + 2 * CELL_SIZE, START_POS[1] + 1.5 * CELL_SIZE])