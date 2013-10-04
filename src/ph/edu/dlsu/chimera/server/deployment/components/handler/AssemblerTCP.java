/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

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
    public boolean append(PcapPacket segment) {
        if(segment.hasHeader(new Tcp())) {
            Tcp tcp = segment.getHeader(new Tcp());
            if(!this.packetSequence.contains(tcp)) {
                boolean result = this.packetSequence.add(segment);
                PcapPacket latest = this.packetSequence.poll();
                while(latest != null) {
                    Tcp ltcp = latest.getHeader(new Tcp());
                    this.appendTCP(ltcp, latest);
                    latest = this.packetSequence.poll();
                }
                return result;
            }
        }
        return true;
    }

    /**
     * Invoked when tcp packet received. Tcp packets are send to this method in order.
     * @param tcp
     * @param pkt
     */
    protected abstract void appendTCP(Tcp tcp, PcapPacket pkt);
    
}
