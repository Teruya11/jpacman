package nl.tudelft.jpacman.sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import nl.tudelft.jpacman.board.GateState;

/**
 * Draws a gate with its circuit identifier and physical state.
 */
public final class GateSprite implements Sprite {

    private static final Color CIRCUIT_ONE = new Color(70, 160, 255);
    private static final Color CIRCUIT_TWO = new Color(210, 100, 255);

    private final int circuitId;
    private final GateState state;
    private final Sprite background;

    /**
     * Creates a sprite for a gate.
     *
     * @param circuitId the circuit identifier, either 1 or 2
     * @param state the physical state of the gate
     * @param background the underlying floor sprite
     */
    public GateSprite(
        int circuitId, GateState state, Sprite background
    ) {
        if (circuitId != 1 && circuitId != 2) {
            throw new IllegalArgumentException("Invalid circuit ID: " + circuitId);
        }
        if (state == null || background == null) {
            throw new IllegalArgumentException(
                "State and background cannot be null"
            );
        }

        this.circuitId = circuitId;
        this.state = state;
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

            int padding = Math.max(1, width / 10);
            int left = x + padding;
            int top = y + padding;
            int w = Math.max(1, width - 2 * padding);
            int h = Math.max(1, height - 2 * padding);

            Color circuitColor =
                circuitId == 1 ? CIRCUIT_ONE : CIRCUIT_TWO;

            Color gateColor;
            switch (state) {
                case OPEN:
                    gateColor = new Color(50, 190, 100);
                    break;
                case PENDING_CLOSE:
                    gateColor = new Color(255, 175, 40);
                    break;
                case CLOSED:
                default:
                    gateColor = new Color(210, 65, 65);
                    break;
            }

            g.setColor(gateColor);
            g.fillRoundRect(left, top, w, h, width / 5, height / 5);

            g.setColor(circuitColor);
            g.setStroke(new java.awt.BasicStroke(
                Math.max(1f, width / 12f)
            ));
            g.drawRoundRect(left, top, w, h, width / 5, height / 5);

            String label = Integer.toString(circuitId);
            int fontSize = Math.max(9, height / 2);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));

            FontMetrics metrics = g.getFontMetrics();
            int textX = x + (width - metrics.stringWidth(label)) / 2;
            int textY = y + (height - metrics.getHeight()) / 2
                + metrics.getAscent();

            g.setColor(Color.WHITE);
            g.drawString(label, textX, textY);

            // A small state marker near the bottom edge.
            int markerWidth = Math.max(3, width / 4);
            int markerHeight = Math.max(2, height / 10);
            int markerX = x + (width - markerWidth) / 2;
            int markerY = y + height - markerHeight - padding;

            g.setColor(Color.BLACK);
            g.fillRect(markerX, markerY, markerWidth, markerHeight);
        } finally {
            g.dispose();
        }
    }

    @Override
    public Sprite split(int x, int y, int width, int height) {
        return new GateSprite(
            circuitId, state, background.split(x, y, width, height)
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
