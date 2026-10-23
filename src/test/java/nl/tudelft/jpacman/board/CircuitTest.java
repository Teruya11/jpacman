package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircuitTest {

    private Circuit circuit;
    private Gate gate1;
    private Gate gate2;

    @BeforeEach
    void setUp() {
        circuit = new Circuit(1);
        gate1 = new Gate();
        gate2 = new Gate();

        circuit.addGate(gate1);
        circuit.addGate(gate2);
    }

    @Test
    void newCircuitStartsClosed() {
        assertThat(circuit.getId()).isEqualTo(1);
        assertThat(circuit.isOpen()).isFalse();
        assertThat(gate1.getState()).isEqualTo(GateState.CLOSED);
        assertThat(gate2.getState()).isEqualTo(GateState.CLOSED);
    }

    @Test
    void toggleOpensAllGates() {
        circuit.toggle();

        assertThat(circuit.isOpen()).isTrue();
        assertThat(gate1.getState()).isEqualTo(GateState.OPEN);
        assertThat(gate2.getState()).isEqualTo(GateState.OPEN);
    }

    @Test
    void secondToggleClosesAllEmptyGates() {
        circuit.toggle();
        circuit.toggle();

        assertThat(circuit.isOpen()).isFalse();
        assertThat(gate1.getState()).isEqualTo(GateState.CLOSED);
        assertThat(gate2.getState()).isEqualTo(GateState.CLOSED);
    }

    @Test
    void occupiedGateBecomesPendingWhileEmptyGateCloses() {
        circuit.toggle();

        gate1.onOccupancyChanged(true);

        circuit.toggle();

        assertThat(gate1.getState())
            .isEqualTo(GateState.PENDING_CLOSE);
        assertThat(gate2.getState()).isEqualTo(GateState.CLOSED);
    }

    @Test
    void openingCircuitCancelsPendingClosure() {
        circuit.toggle();
        gate1.onOccupancyChanged(true);
        circuit.toggle();

        assertThat(gate1.getState())
            .isEqualTo(GateState.PENDING_CLOSE);

        circuit.toggle();

        assertThat(circuit.isOpen()).isTrue();
        assertThat(gate1.getState()).isEqualTo(GateState.OPEN);
        assertThat(gate2.getState()).isEqualTo(GateState.OPEN);
    }

    @Test
    void invalidCircuitIdIsRejected() {
        assertThat(org.assertj.core.api.Assertions.catchThrowable(
            () -> new Circuit(3)
        )).isInstanceOf(IllegalArgumentException.class);
    }
}

