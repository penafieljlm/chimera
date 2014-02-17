package ph.edu.dlsu.chimera.monitors;

import java.util.HashMap;
import ph.edu.dlsu.chimera.components.Component;

/**
 *
 * @author AMD
 */
public abstract class PhaseMonitor {

    public final long updateInterval;
    private boolean terminate;
    private HashMap<String, Component> components;

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

    public void setComponents(HashMap<String, Component> components) {
        this.components = components;
    }

    public HashMap<String, Component> getComponents() {
        return this.components;
    }
}
