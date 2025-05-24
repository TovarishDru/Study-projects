import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Class that inputs the data, implements all further actions of insects on the board and outputs the result
 */
public class Main {
    private static Board gameBoard;
    private static final int TO_STRING_CONST = 6;
    private static final int MIN_CONST = 1;
    private static final int MIN_BOARD_SIZE = 4;
    private static final int BOARD_MAX_SIZE = 1000;
    private static final int MAX_NUMBER_OF_INSECTS = 16;
    private static final int MAX_NUMBER_OF_FOOD_POINTS = 200;

    /**
     * Method that controls the input and output, executes all actions with insects
     * @param args Parameter of main by default
     */
    public static void main(String[] args) {
        try {
            InputStream input = new FileInputStream("input.txt");
            OutputStream output = new FileOutputStream("output.txt");
            PrintStream printStream = new PrintStream(output);
            System.setIn(input);
            System.setOut(printStream);
            Scanner s = new Scanner(System.in);
            List<EntityPosition> insectsPositions = new ArrayList<>();
            getInput(s, insectsPositions);
            EntityPosition entityPosition;
            Insect insect;
            Iterator<BoardEntity> iterator = gameBoard.getBoardData().values().iterator();
            for (int i = 0; i < insectsPositions.size(); i++) {
                entityPosition = insectsPositions.get(i);
                insect = (Insect) gameBoard.getEntity(entityPosition);
                System.out.println(insect.color.getTextRepresentation() + " "
                        + insect.getClass().toString().substring(TO_STRING_CONST) + " "
                        + gameBoard.getDirection(insect).getTextRepresentation() + " "
                        + gameBoard.getDirectionSum(insect));
            }
            input.close();
            output.close();
        } catch (IOException | InvalidBoardSizeException | InvalidNumberOfInsects | InvalidNumberOfFoodPoints
                 | InvalidInsectColorException | InvalidInsectTypeException | InvalidEntityPosition
                 | TwoEntitiesOnSamePositionException | DuplicateInsectException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Method that inputs the string and converts it to a InsectColor or throws an exception if color is not valid
     * @param s Scanner object to input the string
     * @return InsectColor object - represents the color of an insect
     * @throws InvalidInsectColorException Exception thrown if the name of a color is not valid
     */
    public static InsectColor readColor(Scanner s) throws InvalidInsectColorException {
        String color = s.next();
        return InsectColor.toColor(color);
    }

    /**
     * Method that inputs string and converts it to an InsectType or throws an exception if insect name is not valid
     * @param s Scanner object to input the string
     * @return InsectType object - represents the type of insect
     * @throws InvalidInsectTypeException Exception thrown if the name of an insect is not valid
     */
    public static InsectType readInsectType(Scanner s) throws InvalidInsectTypeException {
        String insect = s.next();
        return InsectType.toInsect(insect);
    }

    /**
     * Method that inputs integer coordinate and check its validity
     * @param s Scanner to input the integer
     * @param boardSize Integer size of the board
     * @return Returns integer coordinate if it is valid
     * @throws InvalidEntityPosition Thrown exception if coordinate is not valid
     */
    public static int readCoordinate(Scanner s, int boardSize) throws InvalidEntityPosition {
        int coordinate = s.nextInt();
        if (coordinate < 1 || coordinate > boardSize) {
            throw new InvalidEntityPosition();
        }
        return coordinate;
    }

    /**
     * Method that inputs all data and fills board
     * @param s Scanner object to input the data
     * @param insectsPositions List of EntityPositions with positions of all insects
     * @throws InvalidBoardSizeException Exception thrown if board size is invalid
     * @throws InvalidNumberOfInsects Exception thrown if number of Insects is invalid
     * @throws InvalidNumberOfFoodPoints Exception thrown if number of FoodPoints is invalid
     * @throws InvalidInsectColorException Exception thrown if color of Insect is not valid
     * @throws InvalidInsectTypeException Exception thrown if type of Insect is not valid
     * @throws InvalidEntityPosition Exception thrown if coordinates of a BoardEntity are not valid
     * @throws DuplicateInsectException Exception thrown if two Insects of the same color and type were found
     * @throws TwoEntitiesOnSamePositionException Exception thrown if two BoardEntities are placed in the same location
     */
    public static void getInput(Scanner s, List<EntityPosition> insectsPositions) throws InvalidBoardSizeException,
            InvalidNumberOfInsects, InvalidNumberOfFoodPoints, InvalidInsectColorException,
            InvalidInsectTypeException, InvalidEntityPosition, DuplicateInsectException,
            TwoEntitiesOnSamePositionException {
        int d;
        int n;
        int m;
        EntityPosition entityPosition;
        InsectColor insectColor;
        InsectType insectType;
        BoardEntity newEntity = null;
        d = s.nextInt();
        if (d < MIN_BOARD_SIZE || d > BOARD_MAX_SIZE) {
            throw new InvalidBoardSizeException();
        }
        gameBoard = new Board(d);
        n = s.nextInt();
        if (n < MIN_CONST || n > MAX_NUMBER_OF_INSECTS) {
            throw new InvalidNumberOfInsects();
        }
        m = s.nextInt();
        if (m < MIN_CONST || m > MAX_NUMBER_OF_FOOD_POINTS) {
            throw new InvalidNumberOfFoodPoints();
        }
        for (int i = 0; i < n; i++) {
            insectColor = readColor(s);
            insectType = readInsectType(s);
            int x = readCoordinate(s, d);
            int y = readCoordinate(s, d);
            entityPosition = new EntityPosition(x, y);
            switch (insectType) {
                case ANT:
                    newEntity = new Ant(entityPosition, insectColor);
                    break;
                case SPIDER:
                    newEntity = new Spider(entityPosition, insectColor);
                    break;
                case BUTTERFLY:
                    newEntity = new Butterfly(entityPosition, insectColor);
                    break;
                case GRASSHOPPER:
                    newEntity = new Grasshopper(entityPosition, insectColor);
                    break;
                default:
                    throw new InvalidInsectTypeException();
            }
            Iterator<Map.Entry<String, BoardEntity>> iterator = gameBoard.getBoardData().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, BoardEntity> entry = iterator.next();
                String iterPosition = entry.getKey();
                BoardEntity iterEntity = entry.getValue();
                if (iterEntity.getClass() == newEntity.getClass()) {
                    if (((Insect) iterEntity).color == ((Insect) newEntity).color) {
                        throw new DuplicateInsectException();
                    }
                }
                if (Objects.equals(iterPosition, entityPosition.toString())) {
                    throw new TwoEntitiesOnSamePositionException();
                }
            }
            gameBoard.addEntity(newEntity);
            insectsPositions.add(entityPosition);
        }
        for (int i = 0; i < m; i++) {
            int foodAmount = s.nextInt();
            int x = readCoordinate(s, d);
            int y = readCoordinate(s, d);
            entityPosition = new EntityPosition(x, y);
            Iterator<Map.Entry<String, BoardEntity>> iterator = gameBoard.getBoardData().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, BoardEntity> entry = iterator.next();
                String iterPosition = entry.getKey();
                if (Objects.equals(iterPosition, entityPosition.toString())) {
                    throw new TwoEntitiesOnSamePositionException();
                }
            }
            gameBoard.addEntity(new FoodPoint(entityPosition, foodAmount));
        }
    }
}


/**
 * Class that implements functionality of a game board
 */
class Board {
    private Map<String, BoardEntity> boardData = new HashMap<>();
    private int size;

    /**
     * Method that adds a new entity to the board
     * @param entity BoardEntity to add
     */
    public void addEntity(BoardEntity entity) {
        boardData.put(entity.getEntityPosition().toString(), entity);
    }

    /**
     * Method that returns BoardEntity by its coordinates of EntityPosition
     * @param position EntityPosition - coordinates of a BoardEntity
     * @return BoardEntity with coordinates position
     */
    public BoardEntity getEntity(EntityPosition position) {
        return boardData.get(position.toString());
    }
    /**
     * Method that returns the Direction that an Insect will choose to move
     * @param insect Insect object - an insect to be checked
     * @return Direction object - direction that will be chosen
     */
    public Direction getDirection(Insect insect) {
        return insect.getBestDirection(boardData, size);
    }

    /**
     * Method that implements the movement of an Insect on the board
     * @param insect Insect object - insect to be moved
     * @return Integer sum of all values of collected FoodPoints during the movement
     */
    public int getDirectionSum(Insect insect) {
        return insect.travelDirection(this.getDirection(insect), boardData, size);
    }

    /**
     * Method that returns the game board
     * @return Map with all BoardEntities on the board
     */
    public Map<String, BoardEntity> getBoardData() {
        return boardData;
    }

    /**
     * Board constructor
     * @param boardSize Integer size of the board
     */
    public Board(int boardSize) {
        this.size = boardSize;
    }
}


/**
 * Abstract class of all BoardEntities located on the board
 */
abstract class BoardEntity {
    protected EntityPosition entityPosition;
    /**
     * Method to get EntityPosition of a BoardEntity
     * @return EntityPosition - position of a BoardEntity
     */
    public EntityPosition getEntityPosition() {
        return entityPosition;
    }
}


/**
 * Class that represents the position of a BoardEntity on the board
 */
class EntityPosition {
    private int x;
    private int y;
    /**
     * Method to move EntityPosition in North direction
     */
    public void moveNorth() {
        this.x--;
    }
    /**
     * Method to move EntityPosition in South direction
     */
    public void moveSouth() {
        this.x++;
    }
    /**
     * Method to move EntityPosition in West direction
     */
    public void moveWest() {
        this.y--;
    }
    /**
     * Method to move EntityPosition in East direction
     */
    public void moveEast() {
        this.y++;
    }
    /**
     * Method to move EntityPosition in North-East direction
     */
    public void moveNorthEast() {
        this.x--;
        this.y++;
    }
    /**
     * Method to move EntityPosition in North-West direction
     */
    public void moveNorthWest() {
        this.x--;
        this.y--;
    }
    /**
     * Method to move EntityPosition in South-East direction
     */
    public void moveSouthEast() {
        this.x++;
        this.y++;
    }
    /**
     * Method to move EntityPosition in South-West direction
     */
    public void moveSouthWest() {
        this.x++;
        this.y--;
    }
    /**
     * Method to check if EntityPosition has valid coordinates
     * @param size Integer size of the board
     * @return Returns true if coordinates are valid, false otherwise
     */
    public boolean onBoard(int size) {
        if (this.x > size || this.y > size || this.x < 1 || this.y < 1) {
            return false;
        }
        return true;
    }
    /**
     * EntityPosition constructor
     * @param x Integer X coordinate
     * @param y Integer Y coordinate
     */
    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString() {
        return x + " " + y;
    }
    /**
     * Method to get X coordinate value
     * @return Integer X coordinate
     */
    public int getX() {
        return x;
    }
    /**
     * Method to get Y coordinate value
     * @return Integer Y coordinate
     */
    public int getY() {
        return y;
    }
    /**
     * Overrides equals, method to check if given EntityPosition equals to this EntityPosition
     * @param obj EntityPosition to be checked
     * @return Returns true if EntityPositions have the same coordinates, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return ((((EntityPosition) obj).getY() == this.y) && (((EntityPosition) obj).getX() == this.x));
    }
    /**
     * Overrides hasCode, method used to make equals method work correctly
     * @return Returns hash(x, y)
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}


/**
 * Class that implements FoodPoint functionality on the board
 */
class FoodPoint extends BoardEntity {
    protected int value;

    /**
     * Method to get value of FoodPoint
     * @return Integer value of FoodPoint
     */
    public int getValue() {
        return value;
    }

    /**
     * FoodPoint constructor
     * @param position EntityPosition potion of a FoodPoint
     * @param value Integer value of a FoodPoint
     */
    public FoodPoint(EntityPosition position, int value) {
        this.entityPosition = position;
        this.value = value;
    }
}


/**
 * Abstract class that describes functionality of all Insects on the board
 */
abstract class Insect extends BoardEntity {
    protected InsectColor color;

    /**
     * Method to get the best direction that Insect will choose to move
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Direction - the direction that an Insect will choose
     */
    public abstract Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize);
    /**
     * Method implements the movement of an Insect on the board
     * @param dir Direction int which Insect will travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer value - the sum of all FoodPoints that and Insect will collect while travelling
     */
    public abstract int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize);

    /**
     * Insect constructor
     * @param position EntityPosition - position in which Insect is located
     * @param color InsectColor - color of Insect
     */
    public Insect(EntityPosition position, InsectColor color) {
        this.color = color;
        this.entityPosition = position;
    }
}


/**
 * Enumeration that represents all valid types of Insects
 */
enum InsectType {
    ANT,
    SPIDER,
    BUTTERFLY,
    GRASSHOPPER;
    /**
     * Method that converts Sting to Insect type object
     * @param s String to be converted
     * @return Return InsectType corresponding to the String
     * @throws InvalidInsectTypeException IException thrown if String represents invalid type of Insect
     */
    public static InsectType toInsect(String s) throws InvalidInsectTypeException {
        s = s.toLowerCase();
        if (Objects.equals(s, "ant")) {
            return InsectType.ANT;
        }
        if (Objects.equals(s, "spider")) {
            return InsectType.SPIDER;
        }
        if (Objects.equals(s, "butterfly")) {
            return InsectType.BUTTERFLY;
        }
        if (Objects.equals(s, "grasshopper")) {
            return InsectType.GRASSHOPPER;
        }
        throw new InvalidInsectTypeException();
    }
}


/**
 * Enumeration that represents all valid colors of Insects
 */
enum InsectColor {
    RED("Red"),
    GREEN("Green"),
    BLUE("Blue"),
    YELLOW("Yellow");
    private final String textRepresentation;
    /**
     * InsectColor constructor
     * @param string String value of InsectColor
     */
    InsectColor(String string) {
        textRepresentation = string;
    }
    /**
     * Method that converts String to InsectColor
     * @param s String to be converted
     * @return InsectColor of Insect
     * @throws InvalidInsectColorException Exception thrown if String represents invalid color
     */
    public static InsectColor toColor(String s) throws InvalidInsectColorException {
        s = s.toLowerCase();
        if (Objects.equals(s, "red")) {
            return InsectColor.RED;
        }
        if (Objects.equals(s, "green")) {
            return InsectColor.GREEN;
        }
        if (Objects.equals(s, "blue")) {
            return InsectColor.BLUE;
        }
        if (Objects.equals(s, "yellow")) {
            return InsectColor.YELLOW;
        }
        throw new InvalidInsectColorException();
    }
    /**
     * Method to get String representation of InsectColor
     * @return String representation
     */
    public String getTextRepresentation() {
        return textRepresentation;
    }
}


/**
 * Enumeration that represents all possible Directions to travel
 */
enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");
    private final String textRepresentation;
    /**
     * Direction constructor
     * @param text String value of Direction
     */
    Direction(String text) {
        this.textRepresentation = text;
    }
    /**
     * Method to get String representation of Direction
     * @return String representation
     */
    public String getTextRepresentation() {
        return textRepresentation;
    }
}


/**
 * Class that implements Butterflies functionality on the board
 */
class Butterfly extends Insect implements OrthogonalMoving {
    /**
     * Method to get the Direction that Butterfly will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Direction - direction that Butterfly will choose
     */
    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return getBestOrthogonalDirection(boardData, this.entityPosition, boardSize);
    }
    /**
     * Method that implements the movement of a Butterfly on the board
     * @param dir Direction int which Insect will travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of all values of collected FoodPoints during the movement
     */
    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelOrthogonally(dir, this.entityPosition, this.color, boardData, boardSize);
    }
    /**
     * Butterfly constructor
     * @param entityPosition EntityPosition - position of Butterfly on the board
     * @param color InsectColor - color of Butterfly
     */
    public Butterfly(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
}


/**
 * Class that implements Spiders functionality on the board
 */
class Spider extends Insect implements DiagonalMoving {
    /**
     * Method to get the Direction that Spider will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Direction - direction that Spider will choose
     */
    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return getBestDiagonalDirection(boardData, this.entityPosition, boardSize);
    }
    /**
     * Method that implements the movement of a Spider on the board
     * @param dir Direction int which Insect will travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of all values of collected FoodPoints during the movement
     */
    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelDiagonally(dir, this.entityPosition, this.color, boardData, boardSize);
    }
    /**
     * Spider constructor
     * @param entityPosition EntityPosition - position of Spider on the board
     * @param color InsectColor - color of Spider
     */
    public Spider(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
}


/**
 * Class that implements Ants functionality on the board
 */
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {
    /**
     * Method to get the Direction that Ant will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Direction - direction that Spider will choose
     */
    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction maxOrthogonalDirection = getBestOrthogonalDirection(boardData, entityPosition, boardSize);
        Direction maxDiagonalDirection = getBestDiagonalDirection(boardData, entityPosition, boardSize);
        int sumOrthogonalDirection = getOrthogonalDirectionVisibleValue(maxOrthogonalDirection, this.entityPosition,
                boardData, boardSize);
        int sumDiagonalDirection = getDiagonalDirectionVisibleValue(maxDiagonalDirection, this.entityPosition,
                boardData, boardSize);
        if (sumOrthogonalDirection >= sumDiagonalDirection) {
            return maxOrthogonalDirection;
        }
        return maxDiagonalDirection;
    }
    /**
     * Method that implements the movement of an Ant on the board
     * @param dir Direction int which Insect will travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of all values of collected FoodPoints during the movement
     */
    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        switch (dir) {
            case W:
                return travelOrthogonally(dir, entityPosition, this.color, boardData, boardSize);
            case S:
                return travelOrthogonally(dir, entityPosition, this.color, boardData, boardSize);
            case N:
                return travelOrthogonally(dir, entityPosition, this.color, boardData, boardSize);
            case E:
                return travelOrthogonally(dir, entityPosition, this.color, boardData, boardSize);
            case SE:
                return travelDiagonally(dir, entityPosition, this.color, boardData, boardSize);
            case NW:
                return travelDiagonally(dir, entityPosition, this.color, boardData, boardSize);
            case SW:
                return travelDiagonally(dir, entityPosition, this.color, boardData, boardSize);
            case NE:
                return travelDiagonally(dir, entityPosition, this.color, boardData, boardSize);
            default:
                return -1;
        }
    }
    /**
     * Ant constructor
     * @param entityPosition EntityPosition - position of Ant on the board
     * @param color InsectColor - color of Ant
     */
    public Ant(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
}


/**
 * Class that implements Grasshopper functionality on the board
 */
class Grasshopper extends Insect implements JumpyOrthogonalMoving {
    /**
     * Method to get the Direction that Grasshopper will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Direction - direction that Grasshopper will choose
     */
    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return getBestJumpyOrthogonalDirection(boardData, entityPosition, boardSize);
    }
    /**
     * Method that implements the movement of a Grasshopper on the board
     * @param dir Direction int which Insect will travel
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of all values of collected FoodPoints during the movement
     */
    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelJumpyOrthogonally(dir, this.entityPosition, this.color, boardData, boardSize);
    }

    /**
     * Grasshopper constructor
     * @param entityPosition EntityPosition - position of Grasshopper on the board
     * @param color InsectColor - color of Grasshopper
     */
    Grasshopper(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
}


/**
 * Interface that describes orthogonal movements with jump by two cells
 */
interface JumpyOrthogonalMoving extends Moving {
    /**
     * Method to get the sum of all FoodPoints that an Insect may get while travelling a direction
     * @param dir Direction to be checked
     * @param entityPosition EntityPosition - position of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect may collect
     */
    public default int getJumpyOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                               Map<String, BoardEntity> boardData,
                                                               int boardSize) {
        return getVisibleValue(dir, entityPosition, boardData, boardSize, SHIFT_BY_2);
    }
    /**
     * Method that implements orthogonal movement with jump by two cells
     * @param dir Direction to travel
     * @param entityPosition EntityPosition - position of Insect
     * @param color EntityColor - color of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect will collect
     */
    public default int travelJumpyOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                               Map<String, BoardEntity> boardData, int boardSize) {
        return travel(dir, entityPosition, color, boardData, boardSize, SHIFT_BY_2);
    }
    /**
     * Method to choose the direction that an Insect will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param entityPosition EntityPosition - position of Insect
     * @param boardSize Integer size of the board
     * @return Direction - direction that Insect will choose to travel
     */
    public default Direction getBestJumpyOrthogonalDirection(Map<String, BoardEntity> boardData,
                                                             EntityPosition entityPosition, int boardSize) {
        int southDirection = getJumpyOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int northDirection = getJumpyOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int westDirection = getJumpyOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize);
        int eastDirection = getJumpyOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int maxDirection = Integer.max(southDirection, Integer.max(northDirection,
                Integer.max(westDirection, eastDirection)));
        if (maxDirection == northDirection) {
            return Direction.N;
        }
        if (maxDirection == eastDirection) {
            return Direction.E;
        }
        if (maxDirection == southDirection) {
            return Direction.S;
        }
        return Direction.W;
    }
}


/**
 * Interface that describes basic orthogonal movements
 */
interface OrthogonalMoving extends Moving {
    /**
     * Method to get the sum of all FoodPoints that an Insect may get while travelling a direction
     * @param dir Direction to be checked
     * @param entityPosition EntityPosition - position of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect may collect
     */
    public default int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                          Map<String, BoardEntity> boardData, int boardSize) {
        return getVisibleValue(dir, entityPosition, boardData, boardSize, SHIFT_BY_1);
    }
    /**
     * Method that implements basic orthogonal movement
     * @param dir Direction to travel
     * @param entityPosition EntityPosition - position of Insect
     * @param color EntityColor - color of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect will collect
     */
    public default int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                          Map<String, BoardEntity> boardData, int boardSize) {
        return travel(dir, entityPosition, color, boardData, boardSize, SHIFT_BY_1);
    }
    /**
     * Method to choose the direction that an Insect will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param entityPosition EntityPosition - position of Insect
     * @param boardSize Integer size of the board
     * @return Direction - direction that Insect will choose to travel
     */
    public default Direction getBestOrthogonalDirection(Map<String, BoardEntity> boardData,
                                                        EntityPosition entityPosition, int boardSize) {
        int southDirection = getOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int northDirection = getOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int westDirection = getOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize);
        int eastDirection = getOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int maxDirection = Integer.max(southDirection, Integer.max(northDirection,
                Integer.max(westDirection, eastDirection)));
        if (maxDirection == northDirection) {
            return Direction.N;
        }
        if (maxDirection == eastDirection) {
            return Direction.E;
        }
        if (maxDirection == southDirection) {
            return Direction.S;
        }
        return Direction.W;
    }
}


/**
 * Interface that describes basic diagonal movements
 */
interface DiagonalMoving extends Moving {
    /**
     * Method to get the sum of all FoodPoints that an Insect may get while travelling a direction
     * @param dir Direction to be checked
     * @param entityPosition EntityPosition - position of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect may collect
     */
    public default int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                        Map<String, BoardEntity> boardData, int boardSize) {
        return getVisibleValue(dir, entityPosition, boardData, boardSize, SHIFT_BY_1);
    }
    /**
     * Method that implements basic diagonal movement
     * @param dir Direction to travel
     * @param entityPosition EntityPosition - position of Insect
     * @param color EntityColor - color of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect will collect
     */
    public default int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                        Map<String, BoardEntity> boardData, int boardSize) {
        return travel(dir, entityPosition, color, boardData, boardSize, SHIFT_BY_1);
    }
    /**
     * Method to choose the direction that an Insect will choose to travel
     * @param boardData Map with all BoardEntities located on the board
     * @param entityPosition EntityPosition - position of Insect
     * @param boardSize Integer size of the board
     * @return Direction - direction that Insect will choose to travel
     */
    public default Direction getBestDiagonalDirection(Map<String, BoardEntity> boardData,
                                                      EntityPosition entityPosition, int boardSize) {
        int northWestDirection = getDiagonalDirectionVisibleValue(Direction.NW, entityPosition, boardData, boardSize);
        int northEastDirection = getDiagonalDirectionVisibleValue(Direction.NE, entityPosition, boardData, boardSize);
        int southWestDirection = getDiagonalDirectionVisibleValue(Direction.SW, entityPosition, boardData, boardSize);
        int southEastDirection = getDiagonalDirectionVisibleValue(Direction.SE, entityPosition, boardData, boardSize);
        int maxDirection = Integer.max(northWestDirection, Integer.max(northEastDirection,
                Integer.max(southWestDirection, southEastDirection)));
        if (maxDirection == northEastDirection) {
            return Direction.NE;
        }
        if (maxDirection == southEastDirection) {
            return Direction.SE;
        }
        if (maxDirection == southWestDirection) {
            return Direction.SW;
        }
        return Direction.NW;
    }
}


/**
 * Interface that describes all possible movements
 */
interface Moving {
    final int SHIFT_BY_1 = 1;
    final int SHIFT_BY_2 = 2;
    /**
     * Method to get the sum of all FoodPoints that an Insect may get while travelling a direction
     * @param dir Direction to be checked
     * @param entityPosition EntityPosition - position of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect may collect
     */
    public default int getVisibleValue(Direction dir, EntityPosition entityPosition, Map<String,
            BoardEntity> boardData, int boardSize, int steps) {
        EntityPosition travelPosition = new EntityPosition(entityPosition.getX(), entityPosition.getY());
        BoardEntity travelEntity;
        int directionSum = 0;
        while (travelPosition.onBoard(boardSize)) {
            travelEntity = boardData.get(getBoardPosition(travelPosition, boardData));
            if (travelEntity instanceof FoodPoint) {
                directionSum += ((FoodPoint) travelEntity).getValue();
            }
            for (int i = 0; i < steps; i++) {
                shift(dir, travelPosition);
            }
        }
        return directionSum;
    }
    /**
     * Method that implements movement
     * @param dir Direction to travel
     * @param entityPosition EntityPosition - position of Insect
     * @param color EntityColor - color of Insect
     * @param boardData Map with all BoardEntities located on the board
     * @param boardSize Integer size of the board
     * @return Integer sum of FoodPoints that Insect will collect
     */
    public default int travel(Direction dir, EntityPosition entityPosition, InsectColor color,
                              Map<String, BoardEntity> boardData, int boardSize, int steps) {
        EntityPosition travelPosition = new EntityPosition(entityPosition.getX(), entityPosition.getY());
        BoardEntity travelEntity;
        boardData.remove(entityPosition.toString());
        int directionSum = 0;
        while (travelPosition.onBoard(boardSize)) {
            travelEntity = boardData.get(getBoardPosition(travelPosition, boardData));
            if (travelEntity instanceof FoodPoint) {
                directionSum += ((FoodPoint) travelEntity).getValue();
                boardData.remove(getBoardPosition(travelPosition, boardData));
            }
            if (travelEntity instanceof Insect) {
                if (((Insect) travelEntity).color != color) {
                    return directionSum;
                }
            }
            for (int i = 0; i < steps; i++) {
                shift(dir, travelPosition);
            }
        }
        return directionSum;
    }
    /**
     * Method that shifts the EntityObject coordinates in the given Direction
     * @param dir Direction - shift direction
     * @param travelPosition TravelPosition - position of BoardEntity to be shifted
     */
    public default void shift(Direction dir, EntityPosition travelPosition) {
        switch (dir) {
            case W:
                travelPosition.moveWest();
                break;
            case E:
                travelPosition.moveEast();
                break;
            case N:
                travelPosition.moveNorth();
                break;
            case S:
                travelPosition.moveSouth();
                break;
            case NE:
                travelPosition.moveNorthEast();
                break;
            case NW:
                travelPosition.moveNorthWest();
                break;
            case SE:
                travelPosition.moveSouthEast();
                break;
            case SW:
                travelPosition.moveSouthWest();
                break;
            default:
                return;
        }
    }
    /**
     * Method that finds EntityPosition with the same coordinates with given entityPosition on the board
     * @param entityPosition EntityPosition - coordinates to be found
     * @param boardData Map with all BoardEntities located on the board
     * @return EntityPosition with the same coordinates on the board, null if it was not found
     */
    public default String getBoardPosition(EntityPosition entityPosition, Map<String,
            BoardEntity> boardData) {
        Iterator<Map.Entry<String, BoardEntity>> iterator = boardData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BoardEntity> entry = iterator.next();
            String iterPosition = entry.getKey();
            BoardEntity iterEntity = entry.getValue();
            if (Objects.equals(entityPosition.toString(), iterPosition)) {
                return iterPosition;
            }
        }
        return null;
    }
}


/**
 * Exception that is thrown in case of invalid board size
 */
class InvalidBoardSizeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}


/**
 * Exception that is thrown in case of invalid number of insects
 */
class InvalidNumberOfInsects extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of insects";
    }
}


/**
 * Exception that is thrown in case of invalid number of food points
 */
class InvalidNumberOfFoodPoints extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of food points";
    }
}


/**
 * Exception that is thrown in case of invalid insect color
 */
class InvalidInsectColorException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid insect color";
    }
}


/**
 * Exception that is thrown in case of invalid insect type
 */
class InvalidInsectTypeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid insect type";
    }
}


/**
 * Exception that is thrown in case of invalid entity position
 */
class InvalidEntityPosition extends Exception {
    @Override
    public String getMessage() {
        return "Invalid entity position";
    }
}


/**
 * Exception that is thrown in case of insects being duplicated
 */
class DuplicateInsectException extends Exception {
    @Override
    public String getMessage() {
        return "Duplicate insects";
    }
}


/**
 * Exception that is thrown in case of two entities have the same position
 */
class TwoEntitiesOnSamePositionException extends Exception {
    @Override
    public String getMessage() {
        return "Two entities in the same position";
    }
}
