package nl.tudelft.jpacman.board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

/**
 * Controls the open/close command shared by a group of gates.
 */
public final class Circuit {

    private final int id;
    private final List<Gate> gates = new ArrayList<>();
    private boolean open;
    private long timeoutMillis = 5000;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> disableFuture;

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
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
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

    // Chamado na coleta
    public void startPhaseInversion() {
        if (this.disableFuture == null) {
            this.toggle();
        } else {
            this.disableFuture.cancel(true);
        }

        this.disableFuture = scheduler.schedule(
            new DisableInversionTask(scheduler, this),
            this.timeoutMillis,
            TimeUnit.MILLISECONDS
        );
    }

    // Quando o jogo é pausado
    public void stopPhaseInversion() {
        if (this.disableFuture == null)
            return;

        this.timeoutMillis = this.disableFuture.getDelay(TimeUnit.MILLISECONDS);
        this.disableFuture.cancel(true);
    }

    // Quando o jogo é despausado?
    public void resumePhaseInversion() {
        if (this.disableFuture == null)
            return;

        this.disableFuture = scheduler.schedule(
            new DisableInversionTask(scheduler, this),
            this.timeoutMillis,
            TimeUnit.MILLISECONDS
        );
        this.timeoutMillis = 5000;
    }

    private final class DisableInversionTask implements Runnable {
        private final ScheduledExecutorService scheduler;
        private final Circuit circuit;

        DisableInversionTask(ScheduledExecutorService scheduler, Circuit circuit) {
            this.scheduler = scheduler;
            this.circuit = circuit;
        }

        @Override
        public void run() {
            this.circuit.toggle();
            this.circuit.disableFuture = null;
        }
    }
}


