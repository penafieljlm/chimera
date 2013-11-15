/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.assembler;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.pdu.PduComposite;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.util.TimeUtils;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembler extends Statistics {
    
    private ConcurrentLinkedQueue<PduComposite> queue;
    private long currentConstructionStartedMs;

    public Assembler(long timeCreatedNanos) {
        super(timeCreatedNanos);
        this.queue = new ConcurrentLinkedQueue<>();
        this.currentConstructionStartedMs = TimeUtils.nowMs();
    }

    public long getTimeCurrentConstructionStartedMs() {
        return this.currentConstructionStartedMs;
    }

    public long getOngoingConstructionTimeMs() {
        return TimeUtils.nowMs() - this.currentConstructionStartedMs;
    }

    public PduComposite poll() {
        return this.queue.poll();
    }

    public int size() {
        return this.queue.size();
    }

    public void append(PduAtomic segment) {
        super.commitEncounter(segment);
    }

    public abstract Assembler createAssemblerInstance(PduAtomic firstPacket);

    protected void outputPDU(PduComposite pdu) {
        this.queue.add(pdu);
        this.currentConstructionStartedMs = TimeUtils.nowMs();
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        Date conststart = new java.sql.Date(this.currentConstructionStartedMs);
        diag.add(new Diagnostic("curconststart", "Time Current Construction Started", conststart.toLocaleString()));
        diag.add(new Diagnostic("curconsttime", "Ongoing Construction Time", this.getOngoingConstructionTimeMs() + "ms"));
        return diag;
    }

    public abstract boolean isAttackDetected();
}
