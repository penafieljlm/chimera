/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.monitors;

import ph.edu.dlsu.chimera.core.Statistics;

/**
 *
 * @author AMD
 */
public class PhaseMonitorStatistics extends PhaseMonitor {

    private Statistics statsMonitor;

    public PhaseMonitorStatistics(long updateInterval) {
        super(updateInterval);
    }

    public void setStatsMonitor(Statistics statsMonitor) {
        this.statsMonitor = statsMonitor;
    }

    public Statistics getStatsMonitor() {
        return this.statsMonitor;
    }
}
