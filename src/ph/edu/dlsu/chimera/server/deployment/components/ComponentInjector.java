/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentInjector extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final Pcap outPcap;
    private long sent;

    public ComponentInjector(Assembly assembly, ConcurrentLinkedQueue<PduAtomic> inQueue, Pcap outPcap) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inQueue = inQueue;
        this.outPcap = outPcap;
        this.sent = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PduAtomic pkt = this.inQueue.poll();
                    if (this.outPcap != null) {
                        this.outPcap.sendPacket(pkt.packet.getHeader(new Ethernet()));
                    } else {
                        throw new Exception("Error: [Injector] Unable to access sending device.");
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
