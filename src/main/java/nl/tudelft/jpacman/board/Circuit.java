package nl.tudelft.jpacman.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the open/close command shared by a group of gates.
 */
public final class Circuit {

    private final int id;
    private final List<Gate> gates = new ArrayList<>();
    private boolean open;

    /**
     * Creates a circuit that starts closed.
     *
     * @param id circuit identifier (1 or 2)
     */
    public Circuit(int id) {
        if (id != 1 && id != 2) {
            throw new IllegalArgumentException(
                "Invalid circuit ID: " + id
            );
        }

        this.id = id;
        this.open = false;
    }

    public int getId() {
        return id;
    }

    public boolean isOpen() {
        return open;
    }

    /**
     * Registers a gate controlled by this circuit.
     *
     * @param gate gate to register
     */
    public void addGate(Gate gate) {
        if (gate == null) {
            throw new IllegalArgumentException("Gate cannot be null");
        }

        if (!gates.contains(gate)) {
            gates.add(gate);

            if (open) {
                gate.requestOpen();
            } else {
                gate.requestClose(gate.isOccupied());
            }
        }
    }

    /**
     * Toggles the command and applies it to every registered gate.
     */
    public void toggle() {
        open = !open;

        for (Gate gate : gates) {
            if (open) {
                gate.requestOpen();
            } else {
                gate.requestClose(gate.isOccupied());
            }
        }
    }
}

