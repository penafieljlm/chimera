/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;
import ph.edu.dlsu.chimera.util.ToolsArray;
import ph.edu.dlsu.chimera.util.ToolsTime;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembler extends Statistics {

    public static final int ASSM_CSV_VAL_COUNT = 1;
    private ConcurrentLinkedQueue<PduComposite> queue;
    private long currentConstructionStartedMs;

    public Assembler(long timeCreatedNanos) {
        super(timeCreatedNanos);
        this.queue = new ConcurrentLinkedQueue<PduComposite>();
        this.currentConstructionStartedMs = ToolsTime.nowMs();
    }

    public long getTimeCurrentConstructionStartedMs() {
        return this.currentConstructionStartedMs;
    }

    public long getOngoingConstructionTimeMs() {
        return ToolsTime.nowMs() - this.currentConstructionStartedMs;
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
        this.currentConstructionStartedMs = ToolsTime.nowMs();
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        Date conststart = new java.sql.Date(this.currentConstructionStartedMs);
        diag.add(new Diagnostic("curconststart", "Time Current Construction Started", conststart.toLocaleString()));
        diag.add(new Diagnostic("curconsttime", "Ongoing Construction Time", this.getOngoingConstructionTimeMs() + "ms"));
        return diag;
    }

    @Override
    public synchronized String[] toCsvValues() {
        String[] val = new String[Assembler.ASSM_CSV_VAL_COUNT];
        val[0] = "" + this.getOngoingConstructionTimeMs();
        return ToolsArray.concat(super.toCsvValues(), val);
    }
}
