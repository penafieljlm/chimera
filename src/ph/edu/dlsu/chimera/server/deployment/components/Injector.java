/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Injector extends Component {

    public final String device;
    protected final ConcurrentLinkedQueue<PcapPacket> inQueue;
    protected Pcap pcap;

    public Injector(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue, String device) {
        super(assembly);
        this.device = device;
        this.inQueue = inQueue;
    }

    @Override
    protected void componentRun() {
        this.open();
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PcapPacket front = this.inQueue.poll();
                    this.send(front);
                }
            }
        }
    }

    protected void open() {
        this.pcap = Pcap.openLive(device, Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, null);
    }

    protected void send(PcapPacket pkt) {
        this.pcap.sendPacket(pkt.getHeader(new Ethernet()));
    }
}
