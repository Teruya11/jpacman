
package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.GateSprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * A square controlled by a gate.
 */
public final class GateSquare extends Square {

    private final Gate gate;
    private final int circuitId;
    private final Sprite background;

    /**
     * Creates a square controlled by the given gate.
     *
     * @param gate the gate controlling access
     * @param circuitId the circuit identifier
     * @param background the underlying floor sprite
     */
    public GateSquare(Gate gate, int circuitId, Sprite background) {
        if (gate == null || background == null) {
            throw new IllegalArgumentException(
                "Gate and background cannot be null"
            );
        }
        if (circuitId != 1 && circuitId != 2) {
            throw new IllegalArgumentException(
                "Invalid circuit ID: " + circuitId
            );
        }

        this.gate = gate;
        this.circuitId = circuitId;
        this.background = background;
    }

    public Gate getGate() {
        return gate;
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return gate.canEnter();
    }

    @Override
    void put(Unit occupant) {
        super.put(occupant);
        gate.onOccupancyChanged(!getOccupants().isEmpty());
    }

    @Override
    void remove(Unit occupant) {
        super.remove(occupant);
        gate.onOccupancyChanged(!getOccupants().isEmpty());
    }

    @Override
    public Sprite getSprite() {
        return new GateSprite(circuitId, gate.getState(), background);
    }
}

