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
public class PhaseMonitorTraining extends PhaseMonitor {

    private double progress;
    private String status;

    public PhaseMonitorTraining(long updateInterval) {
        super(updateInterval);
        this.progress = 0.0;
        this.status = "Starting";
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        this.update();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.update();
    }

}
