package nl.tudelft.jpacman;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Circuit;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.GateSquare;
import nl.tudelft.jpacman.board.GateState;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.SwitchSquare;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end smoke tests for the gate and switch feature.
 *
 * These tests launch the real UI and use the real game movement API. The
 * small dedicated map keeps the gate paths deterministic and contains no
 * ghosts, so asynchronous ghost movement cannot make the tests flaky.
 */
public class Feature1SmokeTest {

    private static final String FEATURE_MAP = "/feature1-smoke-map.txt";

    private Launcher launcher;

    @BeforeEach
    void setUp() {
        launcher = new Launcher().withMapFile(FEATURE_MAP);
        launcher.launch();
    }

    @AfterEach
    void tearDown() {
        if (launcher != null) {
            launcher.dispose();
        }
    }

    @Test
    void switchesControlOnlyTheirOwnCircuitAndAllGatesInThatCircuit() {
        Game game = launcher.getGame();
        game.start();

        Board board = game.getLevel().getBoard();
        GateSquare firstCircuitGateTop = gateAt(board, 6, 1);
        GateSquare firstCircuitGateBottom = gateAt(board, 6, 5);
        GateSquare secondCircuitGateTop = gateAt(board, 11, 1);
        GateSquare secondCircuitGateBottom = gateAt(board, 11, 5);

        // Walk onto switch 1.
        move(game, Direction.SOUTH, 2);
        move(game, Direction.EAST, 2);

        assertThat(firstCircuitGateTop.getGate().getState()).isEqualTo(GateState.OPEN);
        assertThat(firstCircuitGateBottom.getGate().getState()).isEqualTo(GateState.OPEN);
        assertThat(secondCircuitGateTop.getGate().getState()).isEqualTo(GateState.CLOSED);
        assertThat(secondCircuitGateBottom.getGate().getState()).isEqualTo(GateState.CLOSED);

        // Leave and step on switch 1 again: repeated activation closes both gates.
        move(game, Direction.WEST, 1);
        move(game, Direction.EAST, 1);
        assertThat(firstCircuitGateTop.getGate().getState()).isEqualTo(GateState.CLOSED);
        assertThat(firstCircuitGateBottom.getGate().getState()).isEqualTo(GateState.CLOSED);

        // Switch 2 opens circuit 2 without changing circuit 1.
        move(game, Direction.EAST, 6);
        assertThat(secondCircuitGateTop.getGate().getState()).isEqualTo(GateState.OPEN);
        assertThat(secondCircuitGateBottom.getGate().getState()).isEqualTo(GateState.OPEN);
        assertThat(firstCircuitGateTop.getGate().getState()).isEqualTo(GateState.CLOSED);
        assertThat(firstCircuitGateBottom.getGate().getState()).isEqualTo(GateState.CLOSED);
    }

    @Test
    void closedGateBlocksPlayerAndPendingClosureCanBeCancelledOrCompleted() {
        Game game = launcher.getGame();
        game.start();

        Board board = game.getLevel().getBoard();
        GateSquare gateSquare = gateAt(board, 6, 1);
        Player player = game.getPlayers().get(0);

        // Attempt to enter the closed gate from the left.
        move(game, Direction.EAST, 5);
        assertThat(player.getSquare()).isSameAs(board.squareAt(5, 1));
        game.move(player, Direction.EAST);
        assertThat(player.getSquare()).isSameAs(board.squareAt(5, 1));
        assertThat(gateSquare.getGate().getState()).isEqualTo(GateState.CLOSED);
        assertThat(gateSquare.isAccessibleTo(
            new GhostFactory(new PacManSprites()).createBlinky())).isFalse();

        // Reach switch 1 and activate circuit 1 through normal player movement.
        move(game, Direction.SOUTH, 2);
        move(game, Direction.WEST, 2);
        assertThat(gateSquare.getGate().getState()).isEqualTo(GateState.OPEN);

        // Enter the open gate, then request closure while the player occupies it.
        move(game, Direction.EAST, 3);
        move(game, Direction.NORTH, 2);
        assertThat(player.getSquare()).isSameAs(gateSquare);

        Circuit circuit = ((SwitchSquare) board.squareAt(3, 3)).getCircuit();
        circuit.toggle();
        assertThat(gateSquare.getGate().getState()).isEqualTo(GateState.PENDING_CLOSE);

        // Reopening cancels the pending closure.
        circuit.toggle();
        assertThat(gateSquare.getGate().getState()).isEqualTo(GateState.OPEN);

        // A subsequent close stays pending until the player leaves the gate.
        circuit.toggle();
        assertThat(gateSquare.getGate().getState()).isEqualTo(GateState.PENDING_CLOSE);
        game.move(player, Direction.SOUTH);
        assertThat(gateSquare.getGate().getState()).isEqualTo(GateState.CLOSED);
    }

    @Test
    void stoppingTheGamePausesMovementAndStartingResumesIt() {
        Game game = launcher.getGame();
        game.start();

        Player player = game.getPlayers().get(0);
        Square initialSquare = player.getSquare();

        game.stop();
        game.move(player, Direction.EAST);
        assertThat(player.getSquare()).isSameAs(initialSquare);

        game.start();
        game.move(player, Direction.EAST);
        assertThat(player.getSquare()).isNotSameAs(initialSquare);
    }

    @Test
    void originalMapStillLoadsAndCanBePlayed() {
        launcher.dispose();
        launcher = new Launcher();
        launcher.launch();

        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        assertThat(game.isInProgress()).isFalse();
        game.start();
        assertThat(game.isInProgress()).isTrue();

        game.move(player, Direction.EAST);
        assertThat(player.getScore()).isEqualTo(10);
        game.stop();
    }

    private void move(Game game, Direction direction, int steps) {
        Player player = game.getPlayers().get(0);
        for (int i = 0; i < steps; i++) {
            game.move(player, direction);
        }
    }

    private GateSquare gateAt(Board board, int x, int y) {
        return (GateSquare) board.squareAt(x, y);
    }
}

