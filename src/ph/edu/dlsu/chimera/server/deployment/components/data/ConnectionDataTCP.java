/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Collections;
import java.util.List;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConnectionDataTCP extends ConnectionData {

    private final List<PcapPacket> queue;
    private boolean synsrc;
    private boolean syndest;
    private long expectedSrcSeq;
    private long expectedDstSeq;
    private boolean done;
    private boolean srcFin;
    private boolean dstFin;

    public ConnectionDataTCP(Connection connection, long timeCreated, boolean inbound) {
        super(connection, timeCreated, inbound);
        this.queue = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.synsrc = false;
        this.syndest = false;
        this.expectedSrcSeq = 0;
        this.expectedDstSeq = 0;
    }

    @Override
    public synchronized void append(PcapPacket pkt) {
        Connection conn = PacketTools.getConnection(pkt);
        if (super.connection.equals(conn)) {
            if (pkt.hasHeader(new Tcp())) {
                super.append(pkt);
                Tcp tcp = pkt.getHeader(new Tcp());
                if (!this.done) {
                    if (tcp.flags_RST()) {
                        //rst anytime
                        this.done = true;
                        this.appendTcp(pkt);
                        return;
                    }
                    if (!this.synsrc && !this.syndest) {
                        //syn
                        if (super.fromSource(pkt)) {
                            if (tcp.flags_SYN()) {
                                this.synsrc = true;
                                this.appendTcp(pkt);
                            }
                        }
                        return;
                    }
                    if (this.synsrc && !this.syndest) {
                        //syn, ack
                        if (super.fromDestination(pkt)) {
                            if (tcp.flags_SYN() && tcp.flags_ACK()) {
                                this.syndest = true;
                                this.expectedSrcSeq = 1;
                                this.expectedDstSeq = 1;
                                this.appendTcp(pkt);
                            }
                        }
                        return;
                    }
                    if (this.synsrc && this.syndest) {
                        //established session
                        
                        //data - always append
                        this.append(pkt);

                        //finish
                        if(this.srcFin && this.dstFin) {
                            this.done = true;
                        }

                        //end sequence
                        if(tcp.flags_FIN()) {
                            if(super.fromSource(pkt) && !this.srcFin) {
                                this.srcFin = true;
                            }
                            if(super.fromDestination(pkt) && !this.dstFin) {
                                this.dstFin = true;
                            }
                        }
                        
                        return;
                    }
                }
            }
        }
    }

    @Override
    public synchronized boolean isDone() {
        return this.done;
    }

    @Override
    public PcapPacket pollFromSource() {
        if (!this.queue.isEmpty()) {
            PcapPacket front = this.queue.get(0);
            if (super.fromSource(front)) {
                Tcp tcp = front.getHeader(new Tcp());
                if (tcp.seq() == this.expectedSrcSeq) {
                    this.expectedSrcSeq += tcp.getPayloadLength();
                    this.queue.remove(front);
                    return front;
                }
            }
        }
        return null;
    }

    @Override
    public PcapPacket pollFromDestination() {
        if (!this.queue.isEmpty()) {
            PcapPacket front = this.queue.get(0);
            if (super.fromDestination(front)) {
                Tcp tcp = front.getHeader(new Tcp());
                if (tcp.seq() == this.expectedDstSeq) {
                    this.expectedSrcSeq += tcp.getPayloadLength();
                    this.queue.remove(front);
                    return front;
                }
            }
        }
        return null;
    }

    private synchronized void appendTcp(PcapPacket pkt) {
        if (!this.hasTcp(pkt)) {
            Tcp tcp = pkt.getHeader(new Tcp());
            long score = tcp.seq() + tcp.ack();
            for (int i = 0; i < this.queue.size(); i++) {
                PcapPacket p = this.queue.get(i);
                Tcp ptcp = p.getHeader(new Tcp());
                long pscore = ptcp.seq() + ptcp.ack();
                if (score <= pscore) {
                    if (score == pscore) {
                        if (tcp.getPayloadLength() <= ptcp.getPayloadLength()) {
                            if (tcp.getPayloadLength() == ptcp.getPayloadLength()) {
                                if (pkt.getCaptureHeader().timestampInNanos() < p.getCaptureHeader().timestampInNanos()) {
                                    this.queue.add(i, pkt);
                                    return;
                                } else {
                                    this.queue.add(i + 1, pkt);
                                    return;
                                }
                            } else {
                                this.queue.add(i, pkt);
                                return;
                            }
                        }
                    } else {
                        this.queue.add(i, pkt);
                        return;
                    }
                }
            }
            this.queue.add(pkt);
            return;
        }
    }

    private synchronized boolean hasTcp(PcapPacket pkt) {
        for (PcapPacket p : this.queue) {
            if (this.tcpAreEqual(p, pkt)) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean tcpAreEqual(PcapPacket pkt1, PcapPacket pkt2) {
        if ((super.fromDestination(pkt1) && super.fromDestination(pkt2)) || (super.fromSource(pkt1) && super.fromSource(pkt2))) {
            //same direction
            Tcp tcp1 = pkt1.getHeader(new Tcp());
            Tcp tcp2 = pkt2.getHeader(new Tcp());
            if (tcp1.seq() == tcp2.seq() && tcp1.ack() == tcp2.ack()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ConnectionData createInstance(Connection connection, long timeCreatedNanoSec, boolean inbound) {
        return new ConnectionDataTCP(connection, timeCreatedNanoSec, inbound);
    }
    
}
