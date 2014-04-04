package ph.edu.dlsu.chimera.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import ph.edu.dlsu.chimera.util.UtilsTime;

/**
 * An instance of this class constitutes an object which keeps track of
 * statistics information. These statistics information can be bound to other
 * identifier objects using HashMaps. Some examples of identifier objects are
 * TcpSocketPairs, and CriteriaInstances. This class is also called the
 * statistics monitor.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Statistics implements IDiagnosable, Serializable {

    /**
     * Time when this statistics monitor has been created (in nanos)
     */
    public final long timeCreatedNanos;
    protected long totalEncounters;
    protected long totalSize;
    protected long lastEncounterNanos;
    protected long lastLastEncounterNanos;

    /**
     * Create a new Statistics object.
     *
     * @param timeCreatedNano Time when this statistics monitor has been created
     * (in nanos)
     */
    public Statistics(long timeCreatedNano) {
        this.timeCreatedNanos = timeCreatedNano;
        this.totalEncounters = 0;
        this.totalSize = 0;
        this.lastEncounterNanos = timeCreatedNano;
        this.lastLastEncounterNanos = -1;
    }

    /**
     *
     * @return The total number of times that an encounter has been committed
     */
    public long getTotalEncounters() {
        return this.totalEncounters;
    }

    /**
     *
     * @return The total size of all commits performed
     */
    public long getTotalSize() {
        return this.totalSize;
    }

    /**
     * Function used to notify this statistics monitor that an encounter had
     * occurred.
     *
     * @param timestampInNanos The timestamp in nanos when the encounter had
     * happened
     * @param size The size of the encountered obejct
     */
    public synchronized void commitEncounter(long timestampInNanos, long size) {
        this.totalEncounters++;
        this.totalSize += size;
        this.lastLastEncounterNanos = this.lastEncounterNanos;
        this.lastEncounterNanos = timestampInNanos;
    }

    public synchronized long getTimeExistedMs() {
        return UtilsTime.nowMs() - UtilsTime.nsToMs((this.timeCreatedNanos));
    }

    public synchronized double getTrafficRatePerSec() {
        double sec = this.getTimeExistedMs() / 1000;
        return (sec > 0) ? this.totalEncounters / sec : this.totalEncounters;
    }

    public synchronized double getAverageSize() {
        return (this.totalEncounters > 0) ? this.totalSize / this.totalEncounters : this.totalSize;
    }

    public synchronized long getLastEncounterTimeNs() {
        return this.lastEncounterNanos;
    }

    public synchronized double getTimeSinceLastEncounterMs() {
        return UtilsTime.nowMs() - UtilsTime.nsToMs((this.lastEncounterNanos));
    }

    public synchronized long getLastEncounterDeltaNs() {
        return (this.lastLastEncounterNanos < 0) ? -1 : this.lastEncounterNanos - this.lastLastEncounterNanos;
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = new ArrayList<Diagnostic>();
        Date create = (this.timeCreatedNanos < 0) ? null : new java.sql.Date(UtilsTime.nsToMs(this.timeCreatedNanos));
        Date update = (this.timeCreatedNanos < 0) ? null : new java.sql.Date(UtilsTime.nsToMs(this.lastEncounterNanos));
        diag.add(new Diagnostic("createtime", "Time Created", (create == null) ? "N/A" : create.toLocaleString()));
        diag.add(new Diagnostic("encounters", "Packets Encountered", this.totalEncounters));
        diag.add(new Diagnostic("totalsize", "Traffic Total Size", this.totalSize));
        diag.add(new Diagnostic("lastencounter", "Last Encounter", (update == null) ? "N/A" : update.toLocaleString()));
        diag.add(new Diagnostic("timexisted", "Time Existed", this.getTimeExistedMs() + "ms"));
        diag.add(new Diagnostic("trafficrate", "Traffic Rate", this.getTrafficRatePerSec() + "pkts/sec"));
        diag.add(new Diagnostic("averagesize", "Traffic Average Size", this.getAverageSize()));
        diag.add(new Diagnostic("idletime", "Idle Time", (this.getLastEncounterTimeNs() < 0) ? "N/A" : this.getTimeSinceLastEncounterMs() + "ms"));
        return diag;
    }

    public Statistics copy() {
        Statistics s = new Statistics(this.timeCreatedNanos);
        s.lastEncounterNanos = this.lastEncounterNanos;
        s.lastLastEncounterNanos = this.lastLastEncounterNanos;
        s.totalEncounters = this.totalEncounters;
        s.totalSize = this.totalSize;
        return s;
    }
}
