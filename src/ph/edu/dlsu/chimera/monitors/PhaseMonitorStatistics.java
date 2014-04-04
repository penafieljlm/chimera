package ph.edu.dlsu.chimera.monitors;

import ph.edu.dlsu.chimera.core.Statistics;

/**
 * An instance of this class constitutes a PhaseMonitor which holds information
 * contained by a Statistics monitor object.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PhaseMonitorStatistics extends PhaseMonitor {

    private Statistics statsMonitor;

    /**
     * Constructs a new PhaseMonitorStatistics object.
     *
     * @param updateInterval The update interval between monitor updates
     */
    public PhaseMonitorStatistics(long updateInterval) {
        super(updateInterval);
    }

    /**
     * Sets the Statistics monitor object.
     *
     * @param statsMonitor The Statistics monitor object
     */
    public void setStatsMonitor(Statistics statsMonitor) {
        this.statsMonitor = statsMonitor;
    }

    /**
     *
     * @return The Statistics monitor object
     */
    public Statistics getStatsMonitor() {
        return this.statsMonitor;
    }
}
