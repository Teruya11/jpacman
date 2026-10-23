package nl.tudelft.jpacman.board;

/**
 * Models the physical state of a gate.
 */
public final class Gate {

    private GateState state;
    private boolean occupied;

    /**
     * Creates a gate that starts closed and unoccupied.
     */
    public Gate() {
        this.state = GateState.CLOSED;
        this.occupied = false;
    }

    /**
     * Returns the current physical state of the gate.
     *
     * @return the gate state
     */
    public GateState getState() {
        return state;
    }

    /**
     * Checks whether an entity may enter the gate.
     *
     * @return true if the gate is open
     */
    public boolean canEnter() {
        return state == GateState.OPEN;
    }

    /**
     * Checks whether the gate is occupied.
     *
     * @return true if an entity occupies the gate
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Opens the gate and cancels any pending closure.
     */
    public void requestOpen() {
        state = GateState.OPEN;
    }

    /**
     * Requests that the gate close.
     *
     * @param occupied whether an entity currently occupies the gate
     */
    public void requestClose(boolean occupied) {
        this.occupied = occupied;

        if (occupied) {
            state = GateState.PENDING_CLOSE;
        } else {
            state = GateState.CLOSED;
        }
    }

    /**
     * Updates the gate when its occupancy changes.
     *
     * @param occupied whether an entity currently occupies the gate
     */
    public void onOccupancyChanged(boolean occupied) {
        this.occupied = occupied;

        if (state == GateState.PENDING_CLOSE && !occupied) {
            state = GateState.CLOSED;
        }
    }
}

