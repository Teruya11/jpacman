
package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GateTest {

    private Gate gate;

    @BeforeEach
    void setUp() {
        gate = new Gate();
    }

    @Test
    void newGateStartsClosed() {
        assertThat(gate.getState()).isEqualTo(GateState.CLOSED);
        assertThat(gate.canEnter()).isFalse();
    }

    @Test
    void openingGateAllowsEntry() {
        gate.requestOpen();

        assertThat(gate.getState()).isEqualTo(GateState.OPEN);
        assertThat(gate.canEnter()).isTrue();
    }

    @Test
    void closingEmptyGateIsImmediate() {
        gate.requestOpen();

        gate.requestClose(false);

        assertThat(gate.getState()).isEqualTo(GateState.CLOSED);
        assertThat(gate.canEnter()).isFalse();
    }

    @Test
    void closingOccupiedGateBecomesPending() {
        gate.requestOpen();

        gate.requestClose(true);

        assertThat(gate.getState())
            .isEqualTo(GateState.PENDING_CLOSE);
        assertThat(gate.canEnter()).isFalse();
    }

    @Test
    void pendingGateClosesWhenEmpty() {
        gate.requestOpen();
        gate.requestClose(true);

        gate.onOccupancyChanged(false);

        assertThat(gate.getState()).isEqualTo(GateState.CLOSED);
        assertThat(gate.canEnter()).isFalse();
    }

    @Test
    void pendingGateRemainsPendingWhileOccupied() {
        gate.requestOpen();
        gate.requestClose(true);

        gate.onOccupancyChanged(true);

        assertThat(gate.getState())
            .isEqualTo(GateState.PENDING_CLOSE);
        assertThat(gate.canEnter()).isFalse();
    }

    @Test
    void openingPendingGateCancelsClosure() {
        gate.requestOpen();
        gate.requestClose(true);

        gate.requestOpen();

        assertThat(gate.getState()).isEqualTo(GateState.OPEN);
        assertThat(gate.canEnter()).isTrue();
    }
}
