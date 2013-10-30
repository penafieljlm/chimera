/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import com.gremwell.jnetbridge.PcapPort;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentInjector extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final IntermodulePipe<PduAtomic> outQueue;
    public final PcapPort outPcapPort;
    private long sent;

    public ComponentInjector(Assembly assembly, IntermodulePipe<PduAtomic> inQueue, IntermodulePipe<PduAtomic> outQueue, PcapPort outPcapPort) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.outPcapPort = outPcapPort;
        this.sent = 0;
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
                    //poll packet
                    PduAtomic pkt = this.inQueue.poll();
                    this.sent++;
                    if (this.outPcapPort != null) {
                        this.outPcapPort.send(pkt.packet);
                    } else {
                        throw new Exception("Error: [Injector] Unable to access sending device.");
                    }
                    if (this.outQueue != null) {
                        this.outQueue.add(new PduAtomic(pkt.packet, pkt.inbound));
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
