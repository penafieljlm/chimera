/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class HandlerTCP extends ProtocolHandler {

    private ConcurrentLinkedQueue<PcapPacket> messagePackets;

    public HandlerTCP() {
        
    }

    @Override
    public boolean appendPDU(PcapPacket segment) {
        if(segment.hasHeader(new Tcp())) {
            Tcp tcp = segment.getHeader(new Tcp());
            this.appendTCP(tcp, segment);
        }
        return true;
    }

    @Override
    protected void reset() {
        this.messagePackets = new ConcurrentLinkedQueue<PcapPacket>();
    }

    /**
     * Only called when not done.
     * @param pkt
     * @return when done
     */
    protected boolean appendTCP(Tcp tcp, PcapPacket pkt) {
        this.messagePackets.add(pkt);
        return this.isDone();
    }

    protected ConcurrentLinkedQueue<PcapPacket> getMessagePackets() {
        return this.messagePackets;
    }
    
}
