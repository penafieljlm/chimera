/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

import java.util.ArrayList;
import java.util.Date;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.IDiagnosable;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Statistics implements IDiagnosable {

    public final long timeCreatedNanos;
    private long totalEncounters;
    private long totalSize;
    private long lastEncounterNanos;

    public Statistics(long timeCreatedNano) {
        this.timeCreatedNanos = timeCreatedNano;
        this.totalEncounters = 0;
        this.totalSize = 0;
        this.lastEncounterNanos = timeCreatedNano;
    }

    public synchronized void commitEncounter(PduAtomic pkt) {
        this.totalEncounters++;
        this.totalSize += pkt.packet.size();
        this.lastEncounterNanos = pkt.packet.getCaptureHeader().timestampInNanos();
    }

    public synchronized long getTimeExisted() {
        Date now = new Date();
        return now.getTime() - (this.timeCreatedNanos / 1000000);
    }

    public synchronized double getTrafficRate() {
        double sec = this.getTimeExisted() / 1000;
        return (sec > 0) ? this.totalEncounters / sec : this.totalEncounters;
    }

    public synchronized double getAverageSize() {
        return (this.totalEncounters > 0) ? this.totalSize / this.totalEncounters : this.totalSize;
    }

    public synchronized long getLastEncounterTime() {
        return this.lastEncounterNanos;
    }

    public synchronized double getTimeSinceLastEncounter() {
        Date now = new Date();
        return now.getTime() - (this.lastEncounterNanos / 1000000);
    }

    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = new ArrayList<Diagnostic>();
        Date create = (this.timeCreatedNanos < 0) ? null : new java.sql.Date(this.timeCreatedNanos / 1000000);
        Date update = (this.timeCreatedNanos < 0) ? null : new java.sql.Date(this.lastEncounterNanos / 1000000);
        Date lastenc = (this.getLastEncounterTime() < 0) ? null : new java.sql.Date(this.getLastEncounterTime() / 1000000);
        diag.add(new Diagnostic("createtime", "Time Created", (create == null) ? "N/A" : create.toLocaleString()));
        diag.add(new Diagnostic("encounters", "Packets Encountered", this.totalEncounters));
        diag.add(new Diagnostic("totalsize", "Traffic Total Size", this.totalSize));
        diag.add(new Diagnostic("lastencounter", "Last Encounter", (update == null) ? "N/A" : update.toLocaleString()));
        diag.add(new Diagnostic("timexisted", "Time Existed", this.getTimeExisted() + "ms"));
        diag.add(new Diagnostic("trafficrate", "Traffic Rate", this.getTrafficRate() + "pkts/sec"));
        diag.add(new Diagnostic("averagesize", "Traffic Average Size", this.getAverageSize()));
        diag.add(new Diagnostic("lastencounter", "Last Encounter", (lastenc == null) ? "N/A" : lastenc.toLocaleString()));
        diag.add(new Diagnostic("idletime", "Idle Time", (this.getLastEncounterTime() < 0) ? "N/A" : this.getTimeSinceLastEncounter() + "ms"));
        return diag;
    }
}
