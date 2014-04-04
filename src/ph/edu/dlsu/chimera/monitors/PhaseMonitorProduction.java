package ph.edu.dlsu.chimera.monitors;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.logs.Log;

/**
 * An instance of this class constitutes a PhaseMonitor for the Production
 * Phase.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PhaseMonitorProduction extends PhaseMonitorStatistics {

    private ConcurrentLinkedQueue<Log> logs;

    /**
     * Constructs a new PhaseMonitorProduction object.
     *
     * @param updateInterval The update interval between monitor updates
     */
    public PhaseMonitorProduction(long updateInterval) {
        super(updateInterval);
    }

    /**
     *
     * @return The logs created during Production
     */
    public ConcurrentLinkedQueue<Log> getLogs() {
        return this.logs;
    }

    /**
     * Sets the logs list of this Phase monitor
     *
     * @param logs The logs
     */
    public void setLogs(ConcurrentLinkedQueue<Log> logs) {
        this.logs = logs;
        this.update();
    }

}
