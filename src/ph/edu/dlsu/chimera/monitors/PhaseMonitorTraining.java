package ph.edu.dlsu.chimera.monitors;

/**
 * An instance of this class constitutes a PhaseMonitor for the Training Phase.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PhaseMonitorTraining extends PhaseMonitor {

    private double progress;
    private String status;

    /**
     * Constructs a new PhaseMonitorTraining object.
     *
     * @param updateInterval The update interval between monitor updates
     */
    public PhaseMonitorTraining(long updateInterval) {
        super(updateInterval);
        this.progress = 0.0;
        this.status = "Starting";
    }

    /**
     *
     * @return The progress counter value of the Training Phase
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Sets the progress counter value of the Training Phase.
     *
     * @param progress The progress counter value to set
     */
    public void setProgress(double progress) {
        this.progress = progress;
        this.update();
    }

    /**
     *
     * @return The current status of the Training Phase
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the Training Phase.
     *
     * @param status The status to set to this PhaseMonitor object
     */
    public void setStatus(String status) {
        this.status = status;
        this.update();
    }

}
