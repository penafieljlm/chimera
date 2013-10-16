/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.ArrayList;
import java.util.Date;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.IDiagnosable;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;
import ph.edu.dlsu.chimera.util.ToolsTime;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Statistics implements IDiagnosable {

    public final long timeCreatedNanos;
    protected long totalEncounters;
    protected long totalSize;
    protected long lastEncounterNanos;
    protected long lastLastEncounterNanos;

    public Statistics(long timeCreatedNano) {
        this.timeCreatedNanos = timeCreatedNano;
        this.totalEncounters = 0;
        this.totalSize = 0;
        this.lastEncounterNanos = timeCreatedNano;
        this.lastLastEncounterNanos = -1;
    }

    public long getTotalEncounters() {
        return this.totalEncounters;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public synchronized void commitEncounter(Pdu pkt) {
        this.totalEncounters++;
        this.totalSize += pkt.size();
        this.lastLastEncounterNanos = this.lastEncounterNanos;
        this.lastEncounterNanos = pkt.timestampInNanos();
    }

    public synchronized long getTimeExistedMs() {
        return ToolsTime.nowMs() - ToolsTime.nsToMs((this.timeCreatedNanos));
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
        return ToolsTime.nowMs() - ToolsTime.nsToMs((this.lastEncounterNanos));
    }

    public synchronized long getLastEncounterDeltaNs() {
        return (this.lastLastEncounterNanos < 0) ? -1 : this.lastEncounterNanos - this.lastLastEncounterNanos;
    }

    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = new ArrayList<Diagnostic>();
        Date create = (this.timeCreatedNanos < 0) ? null : new java.sql.Date(ToolsTime.nsToMs(this.timeCreatedNanos));
        Date update = (this.timeCreatedNanos < 0) ? null : new java.sql.Date(ToolsTime.nsToMs(this.lastEncounterNanos));
        Date lastenc = (this.getLastEncounterTimeNs() < 0) ? null : new java.sql.Date(ToolsTime.nsToMs(this.getLastEncounterTimeNs()));
        diag.add(new Diagnostic("createtime", "Time Created", (create == null) ? "N/A" : create.toLocaleString()));
        diag.add(new Diagnostic("encounters", "Packets Encountered", this.totalEncounters));
        diag.add(new Diagnostic("totalsize", "Traffic Total Size", this.totalSize));
        diag.add(new Diagnostic("lastencounter", "Last Encounter", (update == null) ? "N/A" : update.toLocaleString()));
        diag.add(new Diagnostic("timexisted", "Time Existed", this.getTimeExistedMs() + "ms"));
        diag.add(new Diagnostic("trafficrate", "Traffic Rate", this.getTrafficRatePerSec() + "pkts/sec"));
        diag.add(new Diagnostic("averagesize", "Traffic Average Size", this.getAverageSize()));
        diag.add(new Diagnostic("lastencounter", "Last Encounter", (lastenc == null) ? "N/A" : lastenc.toLocaleString()));
        diag.add(new Diagnostic("idletime", "Idle Time", (this.getLastEncounterTimeNs() < 0) ? "N/A" : this.getTimeSinceLastEncounterMs() + "ms"));
        return diag;
    }
}
