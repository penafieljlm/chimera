/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.TcpQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AssemblerTcp extends Assembler {

    public final TcpQueue queue;
    protected final Connection connection;

    public AssemblerTcp() {
        this(-1, null);
    }

    public AssemblerTcp(long timeCreatedNanos, Connection connection) {
        super(timeCreatedNanos);
        this.queue = new TcpQueue();
        this.connection = connection;
    }

    @Override
    public void append(PDUAtomic segment) {
        //will receive tcp packets in order
        if (segment.packet.hasHeader(new Tcp())) {
            this.queue.add(segment);
            PDUAtomic p = this.queue.poll();
            while (p != null) {
                Tcp tcp = p.packet.getHeader(new Tcp());
                this.appendTCP(tcp, p);
            }
        }
        super.append(segment);
    }

    @Override
    public Assembler createAssemblerInstance(PDUAtomic firstPacket) {
        if (firstPacket.packet.hasHeader(new Tcp())) {
            Tcp tcp = firstPacket.packet.getHeader(new Tcp());
            if(tcp.flags_SYN() && !tcp.flags_ACK()) {
                return this.createTcpAssemblerInstance(tcp, firstPacket);
            }
        }
        return null;
    }

    protected abstract AssemblerTcp createTcpAssemblerInstance(Tcp tcp, PDUAtomic firstPacket);

    /**
     * Invoked when tcp packet received. Tcp packets are send to this method in order.
     * @param tcp
     * @param pkt
     */
    protected abstract void appendTCP(Tcp tcp, PDUAtomic pkt);
}
