/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.monitors;

/**
 *
 * @author AMD
 */
public abstract class PhaseMonitorGathering extends PhaseMonitor {

    private long instancesGathered;

    public PhaseMonitorGathering(long updateInterval) {
        super(updateInterval);
        this.instancesGathered = 0;
    }

    public synchronized long getInstancesGathered() {
        return this.instancesGathered;
    }

    public synchronized void setInstancesGathered(long instancesGathered) {
        this.instancesGathered = instancesGathered;
        this.update();
    }

}
