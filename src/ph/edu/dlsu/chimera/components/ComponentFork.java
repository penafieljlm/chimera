/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.IntermodulePipe;
import ph.edu.dlsu.chimera.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentFork extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final IntermodulePipe<PduAtomic>[] outQueues;
    private long processed;

    public ComponentFork(IntermodulePipe<PduAtomic> inQueue,
            IntermodulePipe<PduAtomic>... outQueues) {
        this.inQueue = inQueue;
        this.outQueues = outQueues;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        for (IntermodulePipe<PduAtomic> outQueue : this.outQueues) {
            if (outQueue != null) {
                outQueue.setWriter(this);
            }
        }
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                if (this.inQueue.isEmpty()) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    synchronized (pkt) {
                        if (this.outQueues != null) {
                            this.processed++;
                            for (IntermodulePipe<PduAtomic> outQueue : this.outQueues) {
                                if (outQueue != null) {
                                    outQueue.add(pkt);
                                }
                            }
                        } else {
                            throw new Exception("Error: [Fork] outQueues is null.");
                        }
                    }
                }
            } else {
                throw new Exception("Error: [Fork] inQueue is null.");
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.inQueue != null) {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        }
        if (this.outQueues != null) {
            ArrayList<Diagnostic> oQDiags = new ArrayList<>();
            for (int i = 0; i < this.outQueues.length; i++) {
                oQDiags.add(new Diagnostic("outqueue[" + i + "]", "Outbound Queue " + i + ", Queued Packets", this.outQueues[i].size()));
            }
            diag.add(new Diagnostic("outqueue", "Outbound Queues", oQDiags));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queues", "N/A"));
        }
        diag.add(new Diagnostic("processed", "Packets Processed", this.processed));
        return diag;
    }
}
