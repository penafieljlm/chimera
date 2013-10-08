/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.TcpQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AssemblerTCP extends Assembler {

    public final TcpQueue queue;

    public AssemblerTCP() {
        this.queue = new TcpQueue();
    }

    @Override
    public boolean append(PDUAtomic segment) {
        //will receive tcp packets in order
        if(segment.packet.hasHeader(new Tcp())) {
            this.queue.add(segment);
            PDUAtomic p = this.queue.poll();
            while(p != null) {
                Tcp tcp = p.packet.getHeader(new Tcp());
                this.appendTCP(tcp, p);
            }
        }
        return true;
    }

    /**
     * Invoked when tcp packet received. Tcp packets are send to this method in order.
     * @param tcp
     * @param pkt
     */
    protected abstract void appendTCP(Tcp tcp, PDUAtomic pkt);

}