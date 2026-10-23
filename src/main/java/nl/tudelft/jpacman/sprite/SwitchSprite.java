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
public final class SwitchSprite implements Sprite {

    private static final Color CIRCUIT_ONE = new Color(70, 160, 255);
    private static final Color CIRCUIT_TWO = new Color(210, 100, 255);

    private final int circuitId;
    private final Sprite background;

    /**
     * Creates a switch sprite.
     *
     * @param circuitId the circuit identifier
     * @param background the floor sprite
     */
    public SwitchSprite(int circuitId, Sprite background) {
        if (circuitId != 1 && circuitId != 2) {
            throw new IllegalArgumentException(
                "Invalid circuit ID: " + circuitId
            );
        }
        if (background == null) {
            throw new IllegalArgumentException(
                "Background cannot be null"
            );
        }

        this.circuitId = circuitId;
        this.background = background;
    }

    @Override
    public void draw(
        Graphics graphics, int x, int y, int width, int height
    ) {
        background.draw(graphics, x, y, width, height);

        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int diameter = Math.max(
                1, Math.min(width, height) * 3 / 4
            );
            int left = x + (width - diameter) / 2;
            int top = y + (height - diameter) / 2;

            Color color = circuitId == 1
                ? CIRCUIT_ONE : CIRCUIT_TWO;

            g.setColor(color);
            g.fillOval(left, top, diameter, diameter);

            g.setColor(Color.WHITE);
            g.setFont(new Font(
                Font.SANS_SERIF,
                Font.BOLD,
                Math.max(9, diameter / 2)
            ));

            String label = Integer.toString(circuitId);
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
        return new SwitchSprite(
            circuitId,
            background.split(x, y, width, height)
        );
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }
}
