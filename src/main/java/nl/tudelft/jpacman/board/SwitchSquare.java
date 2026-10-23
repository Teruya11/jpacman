package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.Sprite;
import nl.tudelft.jpacman.sprite.SwitchSprite;

/**
 * A switch that toggles its associated circuit when activated.
 */
public final class SwitchSquare extends Square {

    private final Circuit circuit;
    private final Sprite background;

    /**
     * Creates a switch connected to a circuit.
     *
     * @param circuit the circuit controlled by this switch
     * @param background the underlying floor sprite
     */
    public SwitchSquare(Circuit circuit, Sprite background) {
        if (circuit == null || background == null) {
            throw new IllegalArgumentException(
                "Circuit and background cannot be null"
            );
        }

        this.circuit = circuit;
        this.background = background;
    }

    /**
     * Returns the circuit controlled by this switch.
     *
     * @return the associated circuit
     */
    public Circuit getCircuit() {
        return circuit;
    }

    /**
     * Activates the switch.
     */
    public void activate() {
        circuit.toggle();
    }

    /**
     * Switches do not block movement.
     *
     * @param unit the unit attempting to enter
     * @return true
     */
    @Override
    public boolean isAccessibleTo(Unit unit) {
        return true;
    }

    @Override
    public Sprite getSprite() {
        return new SwitchSprite(circuit.getId(), background);
    }
}
