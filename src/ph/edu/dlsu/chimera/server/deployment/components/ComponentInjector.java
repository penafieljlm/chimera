/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentInjector extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final Pcap outPcap;
    public final PcapDumper dumpPcap;

    public ComponentInjector(Assembly assembly, ConcurrentLinkedQueue<PduAtomic> inQueue, Pcap outPcap, PcapDumper dumpPcap) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inQueue = inQueue;
        this.outPcap = outPcap;
        this.dumpPcap = dumpPcap;
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
                    }
                    if (this.dumpPcap != null) {
                        this.dumpPcap.dump(pkt.packet.getCaptureHeader(), pkt.packet.getHeader(new Ethernet()));
                    }
                }
            }
        }
    }
}
