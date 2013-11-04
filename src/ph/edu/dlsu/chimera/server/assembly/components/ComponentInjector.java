/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components;

import com.gremwell.jnetbridge.PcapPort;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.assembly.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentInjector extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final PcapPort outPcapPort;
    private long sent;

    public ComponentInjector(IntermodulePipe<PduAtomic> inQueue, PcapPort outPcapPort) {
        this.setPriority(Thread.MAX_PRIORITY);
        this.inQueue = inQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        this.outPcapPort = outPcapPort;
        this.sent = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        if (this.outPcapPort == null) {
            throw new Exception("Error: [Injector] Unable to access sending device.");
        }
        while (super.running) {
            if (this.inQueue != null) {
                if (this.inQueue.isEmpty()) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PduAtomic pkt = this.inQueue.poll();
                    synchronized (pkt) {
                        this.sent++;
                        this.outPcapPort.send(pkt.packet);
                    }
                }
            } else {
                throw new Exception("Error: [Injector] inQueue is null.");
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
        diag.add(new Diagnostic("sent", "Packets Sent", this.sent));
        return diag;
    }
}
