package ph.edu.dlsu.chimera.monitors;

import java.util.HashMap;
import ph.edu.dlsu.chimera.components.Component;

/**
 * An instance of this class constitutes an object whose purpose is to be
 * updated by a deployment phase function defined in the Chimera class in order
 * to be able to report about the progress of the deployment phase function
 * mentioned. Also provides a way for asynchronous communication with the system
 * in deployment.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PhaseMonitor {

    /**
     * The update interval between monitor updates
     */
    public final long updateInterval;
    private boolean terminate;
    private HashMap<String, Component> components;

    /**
     * Constructs a new PhaseMonitor object.
     *
     * @param updateInterval The update interval between monitor updates
     */
    public PhaseMonitor(long updateInterval) {
        this.updateInterval = updateInterval;
        this.terminate = false;
    }

    protected void update() {
    }

    /**
     *
     * @return True if the PhaseMonitor wants to signal the phase function to
     * terminate
     */
    public boolean isTerminated() {
        return terminate;
    }

    /**
     * Signals the termination of the phase function that this PhaseMonitor is
     * attached to
     */
    public void terminate() {
        this.terminate = true;
    }

    /**
     * Sets the component assembly that this PhaseMonitor handles.
     *
     * @param components The component assembly that this PhaseMonitor handles
     */
    public void setComponents(HashMap<String, Component> components) {
        this.components = components;
    }

    /**
     *
     * @return The component assembly that this PhaseMonitor handles
     */
    public HashMap<String, Component> getComponents() {
        return this.components;
    }
}
