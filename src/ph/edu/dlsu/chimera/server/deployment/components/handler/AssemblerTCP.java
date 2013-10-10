/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.TcpQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AssemblerTcp extends Assembler {

    public final TcpQueue queue;
    protected ConnectionData connectionData;

    public AssemblerTcp() {
        this.queue = new TcpQueue();
        this.connectionData = null;
    }

    public void setConnectionData(ConnectionData connectionData) {
        this.connectionData = connectionData;
    }

    @Override
    public boolean append(PduAtomic segment) {
        //will receive tcp packets in order
        if (segment.packet.hasHeader(new Tcp())) {
            this.queue.add(segment);
            PduAtomic p = this.queue.poll();
            while (p != null) {
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
    protected abstract void appendTCP(Tcp tcp, PduAtomic pkt);
}
