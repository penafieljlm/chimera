package ph.edu.dlsu.chimera.monitors;

/**
 * An instance of this class constitutes a PhaseMonitor for the Data Gathering
 * Phase.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PhaseMonitorGathering extends PhaseMonitorStatistics {

    private long instancesGathered;

    /**
     * Constructs a new PhaseMonitorGathering object.
     *
     * @param updateInterval The update interval between monitor updates
     */
    public PhaseMonitorGathering(long updateInterval) {
        super(updateInterval);
        this.instancesGathered = 0;
    }

    /**
     *
     * @return The number of instances gathered counter
     */
    public synchronized long getInstancesGathered() {
        return this.instancesGathered;
    }

    /**
     * Sets the number of the gathered instances counter
     *
     * @param instancesGathered The number to set
     */
    public synchronized void setInstancesGathered(long instancesGathered) {
        this.instancesGathered = instancesGathered;
        this.update();
    }

}
