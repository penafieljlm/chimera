package ph.edu.dlsu.chimera.monitors;

/**
 *
 * @author AMD
 */
public abstract class PhaseMonitor {

    public final long updateInterval;
    private boolean terminate;

    public PhaseMonitor(long updateInterval) {
        this.updateInterval = updateInterval;
        this.terminate = false;
    }

    protected abstract void update();

    public boolean isTerminated() {
        return terminate;
    }

    public void terminate() {
        this.terminate = true;
    }
}
