/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConnectionData {

    public final Connection connection;
    public final long timeCreated;
    protected int inboundEncounters;
    protected int outboundEncounters;
    protected final boolean inbound;
    private boolean done;
    private byte srcfin; //0 - none; 1 - fin,ack; 2 - ack
    private byte dstfin; //0 - none; 1 - fin,ack; 2 - ack

    public ConnectionData(Connection connection, long timeCreated, boolean inbound) {
        this.connection = connection;
        this.timeCreated = timeCreated;
        this.inboundEncounters = 0;
        this.outboundEncounters = 0;
        this.inbound = inbound;
        this.done = false;
        this.srcfin = 0;
        this.dstfin = 0;
    }

    /**
     * @return the length of time this TCPState has existed in milliseconds.
     */
    public long getStateTime() {
        Date now = new Date();
        return now.getTime() - this.timeCreated;
    }

    /**
     * @return the inbound rate of this TCPState measured as packet per second.
     */
    public double inboundRate() {
        double sec = this.getStateTime() / 1000;
        return this.inboundEncounters / sec;
    }

    /**
     * @return the outbound rate of this TCPState measured as packet per second.
     */
    public double outboundRate() {
        double sec = this.getStateTime() / 1000;
        return this.outboundEncounters / sec;
    }

    /**
     * Updates the connection data based on the received packet.
     * @param pkt - the received packet.
     */
    public void update(PcapPacket pkt) {
        if (!this.done) {
            Tcp tcp = pkt.getHeader(new Tcp());
            if (this.packetIsFromSource(pkt)) {
                this.inboundEncounters++;
                if(tcp.flags_ACK() && this.dstfin == 1) {
                    this.dstfin = 2;
                }
                if (tcp.flags_FIN() && this.srcfin == 0) {
                    this.srcfin = 1;
                }
            } else {
                this.outboundEncounters++;
                if(tcp.flags_ACK() && this.srcfin == 1) {
                    this.srcfin = 2;
                }
                if (tcp.flags_FIN() && this.dstfin == 0) {
                    this.dstfin = 1;
                }
            }
            if (tcp.flags_RST() || (this.srcfin == 2 && this.dstfin == 2)) {
                this.done = true;
            }
        }
    }

    public boolean isDone() {
        return this.done;
    }

    private boolean packetIsFromSource(PcapPacket pkt) {
        Connection pconn = PacketTools.getConnection(pkt);
        if (this.inbound) {
            return this.connection.source.equals(pconn.source)
                    && this.connection.destination.equals(pconn.destination)
                    && this.connection.sourcePort == pconn.sourcePort
                    && this.connection.destinationPort == pconn.destinationPort;
        } else {
            return this.connection.source.equals(pconn.destination)
                    && this.connection.destination.equals(pconn.source)
                    && this.connection.sourcePort == pconn.destinationPort
                    && this.connection.destinationPort == pconn.sourcePort;
        }
    }
}
