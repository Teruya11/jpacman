package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Circuit;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * Inversor de Fase consumível, herdado de Pellet (Comida)
 *
 * @author Me
 */
public class PhaseInverterPellet extends Unit {

    /**
     * circuito
     */
    private final Circuit circuit;

    private final Sprite image;

    /**
     * Creates a new phase inverter pellet.
     * @param id ID of the associated circuit
     * @param sprite The sprite of this pellet.
     */
    public PhaseInverterPellet(Circuit circuit, Sprite sprite) {
        this.image = sprite;
        this.circuit = circuit;
    }

    /**
     * Returns the point value of this pellet.
     * @return The point value of this pellet.
     */
    public int getId() {
        return circuit.getId();
    }

    @Override
    public Sprite getSprite() {
        return image;
    }

    public void invertCircuit() {
        circuit.startPhaseInversion();        
    }
}
