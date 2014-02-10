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

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }
}
