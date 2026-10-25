package nl.tudelft.jpacman.sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Visual representation of a circuit switch.
 */
public final class PhaseInverterSprite implements Sprite {

    private static final Color CIRCUIT_ONE = new Color(70, 160, 255);
    private static final Color CIRCUIT_TWO = new Color(210, 100, 255);

    Sprite container;
    private int circuitId;
    private int width;
    private int height;

    /**
     * Creates a switch sprite.
     *
     * @param circuitId the circuit identifier
     * @param background the floor sprite
     */
    public PhaseInverterSprite(int circuitId, Sprite container) {
        System.out.println(container);
        if (circuitId != 1 && circuitId != 2) {
            throw new IllegalArgumentException(
                "Invalid circuit ID: " + circuitId
            );
        }

        this.container = container;
        this.circuitId = circuitId;
    }

    @Override
    public void draw(
        Graphics graphics, int x, int y, int width, int height
    ) {
        this.width = width;
        this.height = height;
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
            container.draw(graphics, x, y, width, height);
            
            Color color = circuitId == 1
                ? CIRCUIT_ONE : CIRCUIT_TWO;

            g.setColor(color);
            g.setFont(new Font(
                Font.SANS_SERIF,
                Font.BOLD,
                Math.max(9, height / 2)
            ));

            String label;
            if (circuitId == 1)
                label = "!";
            else
                label = "?";
            FontMetrics metrics = g.getFontMetrics();

            int textX = x + (width - metrics.stringWidth(label)) / 2;
            int textY = y + (height - metrics.getHeight()) / 2
                + metrics.getAscent();

            g.drawString(label, textX, textY);
        } finally {
            g.dispose();
        }
    }

    @Override
    public Sprite split(int x, int y, int width, int height) {
        return new PhaseInverterSprite(circuitId, container);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
