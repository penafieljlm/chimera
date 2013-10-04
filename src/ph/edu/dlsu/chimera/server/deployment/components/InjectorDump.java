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

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class InjectorDump extends Injector {

    public final String filename;
    private PcapDumper dumper;

    public InjectorDump(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue, String device, String filename) {
        super(assembly, inQueue, device);
        this.filename = filename;
    }

    @Override
    protected void open() {
        super.open();
        this.dumper = super.pcap.dumpOpen(this.filename);
    }

    @Override
    protected void send(PcapPacket pkt) {
        super.send(pkt);
        this.dumper.dump(pkt.getCaptureHeader(), pkt.getHeader(new Ethernet()));
    }

}
