/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.ComponentActive;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentInjector extends ComponentActive {

    public final ConcurrentLinkedQueue<PcapPacket> inQueue;
    public final Pcap outPcap;
    public final PcapDumper dumpPcap;

    public ComponentInjector(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue, Pcap outPcap, PcapDumper dumpPcap) {
        super(assembly);
        this.inQueue = inQueue;
        this.outPcap = outPcap;
        this.dumpPcap = dumpPcap;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PcapPacket front = this.inQueue.poll();
                    if (this.outPcap != null) {
                        this.outPcap.sendPacket(front.getHeader(new Ethernet()));
                    }
                    if (this.dumpPcap != null) {
                        this.dumpPcap.dump(front.getCaptureHeader(), front.getHeader(new Ethernet()));
                    }
                }
            }
        }
    }
}
