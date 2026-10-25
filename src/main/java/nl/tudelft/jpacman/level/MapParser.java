package nl.tudelft.jpacman.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Circuit;
import nl.tudelft.jpacman.board.Gate;
import nl.tudelft.jpacman.board.GateSquare;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.SwitchSquare;
import nl.tudelft.jpacman.npc.Ghost;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Creates new {@link Level}s from text representations.
 *
 * @author Jeroen Roosen
 */
public class MapParser {

    /**
     * The factory that creates the levels.
     */
    private final LevelFactory levelCreator;

    /**
     * The factory that creates the squares and board.
     */
    private final BoardFactory boardCreator;

    // Inclusions from GPT
    private Circuit circuit1;
    private Circuit circuit2;

    private int switches1;
    private int switches2;
    private int gates1;
    private int gates2;

    /**
     * Creates a new map parser.
     *
     * @param levelFactory
     *            The factory providing the NPC objects and the level.
     * @param boardFactory
     *            The factory providing the Square objects and the board.
     */
    public MapParser(LevelFactory levelFactory, BoardFactory boardFactory) {
        this.levelCreator = levelFactory;
        this.boardCreator = boardFactory;
    }

    /**
     * Parses the text representation of the board into an actual level.
     *
     * <ul>
     * <li>Supported characters:
     * <li>' ' (space) an empty square.
     * <li>'#' (bracket) a wall.
     * <li>'.' (period) a square with a pellet.
     * <li>'P' (capital P) a starting square for players.
     * <li>'G' (capital G) a square with a ghost.
     * </ul>
     *
     * @param map
     *            The text representation of the board, with map[x][y]
     *            representing the square at position x,y.
     * @return The level as represented by this text.
     */
    public Level parseMap(char[][] map) {
        circuit1 = new Circuit(1);
        circuit2 = new Circuit(2);

        switches1 = 0;
        switches2 = 0;
        gates1 = 0;
        gates2 = 0;
        
        int width = map.length;
        int height = map[0].length;

        Square[][] grid = new Square[width][height];

        List<Ghost> ghosts = new ArrayList<>();
        List<Square> startPositions = new ArrayList<>();

        makeGrid(map, width, height, grid, ghosts, startPositions);
        validateCircuits();

        List<Circuit> circuits = new ArrayList<>();
        circuits.add(circuit1);
        circuits.add(circuit2);
        Board board = boardCreator.createBoard(grid);
        return levelCreator.createLevel(board, ghosts, startPositions, circuits);
    }

    private void makeGrid(char[][] map, int width, int height,
                          Square[][] grid, List<Ghost> ghosts, List<Square> startPositions) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = map[x][y];
                addSquare(grid, ghosts, startPositions, x, y, c);
            }
        }
    }

    /**
     * Adds a square to the grid based on a given character. These
     * character come from the map files and describe the type
     * of square.
     *
     * @param grid
     *            The grid of squares with board[x][y] being the
     *            square at column x, row y.
     * @param ghosts
     *            List of all ghosts that were added to the map.
     * @param startPositions
     *            List of all start positions that were added
     *            to the map.
     * @param x
     *            x coordinate of the square.
     * @param y
     *            y coordinate of the square.
     * @param c
     *            Character describing the square type.
     */
    protected void addSquare(Square[][] grid, List<Ghost> ghosts,
                             List<Square> startPositions, int x, int y, char c) {
        switch (c) {
            case ' ':
                grid[x][y] = boardCreator.createGround();
                break;
            case '#':
                grid[x][y] = boardCreator.createWall();
                break;
            case '.':
                Square pelletSquare = boardCreator.createGround();
                grid[x][y] = pelletSquare;
                levelCreator.createPellet().occupy(pelletSquare);
                break;
            case 'G':
                Square ghostSquare = makeGhostSquare(ghosts, levelCreator.createGhost());
                grid[x][y] = ghostSquare;
                break;
            case 'P':
                Square playerSquare = boardCreator.createGround();
                grid[x][y] = playerSquare;
                startPositions.add(playerSquare);
                break;
            case '1':
                grid[x][y] = boardCreator.createSwitch(circuit1);
                switches1++;
                break;

            case '2':
                grid[x][y] = boardCreator.createSwitch(circuit2);
                switches2++;
                break;

            case 'a':
                Gate gateA = new Gate();
                circuit1.addGate(gateA);
                grid[x][y] = boardCreator.createGate(gateA, 1);
                gates1++;
                break;

            case 'b':
                Gate gateB = new Gate();
                circuit2.addGate(gateB);
                grid[x][y] = boardCreator.createGate(gateB, 2);
                gates2++;
                break;

            case '!':
                Square phase1Square = boardCreator.createGround();
                grid[x][y] = phase1Square;
                levelCreator.createPhasePellet(circuit1).occupy(phase1Square);
                break;

            case '?':
                Square phase2Square = boardCreator.createGround();
                grid[x][y] = phase2Square;
                levelCreator.createPhasePellet(circuit2).occupy(phase2Square);
                break;

            default:
                throw new PacmanConfigurationException("Invalid character at "
                    + x + "," + y + ": " + c);
        }
    }

    /**
     * creates a Square with the specified ghost on it
     * and appends the placed ghost into the ghost list.
     *
     * @param ghosts all the ghosts in the level so far, the new ghost will be appended
     * @param ghost the newly created ghost to be placed
     * @return a square with the ghost on it.
     */
    protected Square makeGhostSquare(List<Ghost> ghosts, Ghost ghost) {
        Square ghostSquare = boardCreator.createGround();
        ghosts.add(ghost);
        ghost.occupy(ghostSquare);
        return ghostSquare;
    }

    /**
     * Parses the list of strings into a 2-dimensional character array and
     * passes it on to {@link #parseMap(char[][])}.
     *
     * @param text
     *            The plain text, with every entry in the list being a equally
     *            sized row of squares on the board and the first element being
     *            the top row.
     * @return The level as represented by the text.
     * @throws PacmanConfigurationException If text lines are not properly formatted.
     */
    public Level parseMap(List<String> text) {

        checkMapFormat(text);

        int height = text.size();
        int width = text.get(0).length();

        char[][] map = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = text.get(y).charAt(x);
            }
        }
        return parseMap(map);
    }

    /**
     * Check the correctness of the map lines in the text.
     * @param text Map to be checked
     * @throws PacmanConfigurationException if map is not OK.
     */
    private void checkMapFormat(List<String> text) {
        if (text == null) {
            throw new PacmanConfigurationException(
                "Input text cannot be null.");
        }

        if (text.isEmpty()) {
            throw new PacmanConfigurationException(
                "Input text must consist of at least 1 row.");
        }

        int width = text.get(0).length();

        if (width == 0) {
            throw new PacmanConfigurationException(
                "Input text lines cannot be empty.");
        }

        for (String line : text) {
            if (line.length() != width) {
                throw new PacmanConfigurationException(
                    "Input text lines are not of equal width.");
            }
        }
    }

    /**
     * Parses the provided input stream as a character stream and passes it
     * result to {@link #parseMap(List)}.
     *
     * @param source
     *            The input stream that will be read.
     * @return The parsed level as represented by the text on the input stream.
     * @throws IOException
     *             when the source could not be read.
     */
    public Level parseMap(InputStream source) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            source, "UTF-8"))) {
            List<String> lines = new ArrayList<>();
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            return parseMap(lines);
        }
    }

    /**
     * Parses the provided input stream as a character stream and passes it
     * result to {@link #parseMap(List)}.
     *
     * @param mapName
     *            Name of a resource that will be read.
     * @return The parsed level as represented by the text on the input stream.
     * @throws IOException
     *             when the resource could not be read.
     */
    @SuppressFBWarnings(
        value = {"OBL_UNSATISFIED_OBLIGATION", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
        justification = "try with resources always cleans up / false positive in java 11"
    )
    public Level parseMap(String mapName) throws IOException {
        try (InputStream boardStream = MapParser.class.getResourceAsStream(mapName)) {
            if (boardStream == null) {
                throw new PacmanConfigurationException("Could not get resource for: " + mapName);
            }
            return parseMap(boardStream);
        }
    }

    /**
     * @return the BoardCreator
     */
    protected BoardFactory getBoardCreator() {
        return boardCreator;
    }

    private void validateCircuits() {
        boolean usesCircuitElements =
            switches1 + switches2 + gates1 + gates2 > 0;

        if (!usesCircuitElements) {
            return;
        }

        if (switches1 == 0 || gates1 == 0) {
            throw new PacmanConfigurationException(
                "Circuit 1 must have at least one switch and one gate.");
        }

        if (switches2 == 0 || gates2 == 0) {
            throw new PacmanConfigurationException(
                "Circuit 2 must have at least one switch and one gate.");
        }
    }
}
