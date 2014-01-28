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
public abstract class PhaseMonitor {

    public final long updateInterval;

    public PhaseMonitor(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    protected abstract void update();

}
