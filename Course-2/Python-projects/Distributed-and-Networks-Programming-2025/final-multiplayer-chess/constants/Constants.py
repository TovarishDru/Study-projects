WHITE = 1
BLACK = 2

WIDTH = 800
HEIGHT = 800
CELL_SIZE = 80
FONT_SIZE = 64

# Position from which board starts drawing
START_POS = (WIDTH // 2 - CELL_SIZE * 4, HEIGHT // 2 + CELL_SIZE * 3 - CELL_SIZE * 0.5)
START_POS_WHITE = START_POS
START_POS_BLACK = (WIDTH // 2 - CELL_SIZE * 4, HEIGHT // 2 - CELL_SIZE * 4 - CELL_SIZE * 0.5)

BACKGROUND_COLOR = 'grey'
BLACK_CELL_COLOR = (123, 148, 93)
WHITE_CELL_COLOR = (238, 237, 213)

SESSIONS_PER_PAGE = 10

# Connect figures_images with their pictures, load at the start of the program
FIGURES_PNG = {}

