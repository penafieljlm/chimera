/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.monitors;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.logs.Log;

/**
 *
 * @author AMD
 */
public class PhaseMonitorProduction extends PhaseMonitorStatistics {

    private ConcurrentLinkedQueue<Log> logs;

    public PhaseMonitorProduction(long updateInterval) {
        super(updateInterval);
    }

    public ConcurrentLinkedQueue<Log> getLogs() {
        return this.logs;
    }

    public void setLogs(ConcurrentLinkedQueue<Log> logs) {
        this.logs = logs;
        this.update();
    }

}
