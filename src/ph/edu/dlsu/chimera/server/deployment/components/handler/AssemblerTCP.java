/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPPacketSequence;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AssemblerTCP extends Assembler {

    private TCPPacketSequence packetSequence;

    public AssemblerTCP() {
        this.packetSequence = new TCPPacketSequence();
    }

    @Override
    public boolean appendPDU(PcapPacket segment) {
        if(segment.hasHeader(new Tcp())) {
            Tcp tcp = segment.getHeader(new Tcp());
            if(!this.packetSequence.contains(tcp)) {
                return this.packetSequence.add(segment);
            }
        }
        return true;
    }

    @Override
    public boolean assemblePDU() {
        if(!this.isDone()) {
            PcapPacket latest = this.packetSequence.poll();
            while(latest != null) {
                Tcp tcp = latest.getHeader(new Tcp());
                this.appendTCP(tcp, latest);
                if(this.isDone())
                    return true;
                latest = this.packetSequence.poll();
            }
            return false;
        }
        return true;
    }

    /**
     * Only called when not done.
     * @param pkt
     * @return when done
     */
    protected boolean appendTCP(Tcp tcp, PcapPacket pkt) {
        this.packetSequence.add(pkt);
        return this.isDone();
    }
    
}
