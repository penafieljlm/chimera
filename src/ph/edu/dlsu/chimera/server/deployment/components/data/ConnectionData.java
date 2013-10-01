/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ConnectionData {

    public final Connection connection;
    public final long timeCreatedNanoSec;
    public final boolean inbound;
    protected int srcCtr;
    protected int dstCtr;

    public ConnectionData(Connection connection, long timeCreatedNanoSec, boolean inbound) {
        this.connection = connection;
        this.timeCreatedNanoSec = timeCreatedNanoSec;
        this.srcCtr = 0;
        this.dstCtr = 0;
        this.inbound = inbound;
    }

    /**
     * @return the length of time this TCPState has existed in nanosecond.
     */
    public synchronized long getStateTime() {
        Date now = new Date();
        return (now.getTime() * 1000000) - this.timeCreatedNanoSec;
    }

    /**
     * @return the source to destination rate of this TCPState measured as packet per second.
     */
    public synchronized double sourceRate() {
        double sec = this.getStateTime() / 1000000000;
        return this.srcCtr / sec;
    }

    /**
     * @return the destination to source rate of this TCPState measured as packet per second.
     */
    public synchronized double destinationRate() {
        double sec = this.getStateTime() / 1000000000;
        return this.dstCtr / sec;
    }

    /**
     * Updates the connection data based on the received packet and appends the packet to this connection.
     * @param pkt - the received packet.
     * @param inbound - true if the packet was received on the interface facing outside.
     */
    public synchronized void append(PcapPacket pkt) {
        Connection conn = PacketTools.getConnection(pkt);
        if (this.connection.equals(conn)) {
            if (this.fromSource(pkt)) {
                this.srcCtr++;
            }
            if (this.fromDestination(pkt)) {
                this.dstCtr++;
            }
        }
    }

    /**
     * @return true if this session is finished.
     */
    public abstract boolean isDone();
    
    /**
     * @return the next packet in this connection going to the destination host.
     */
    public abstract PcapPacket pollFromSource();

    /**
     * @return the next packet in this connection going to the source host.
     */
    public abstract PcapPacket pollFromDestination();

    protected final boolean fromSource(PcapPacket pkt) {
        Connection conn = PacketTools.getConnection(pkt);
        if (conn != null) {
            return this.connection.source.equals(conn.source)
                    && this.connection.sourcePort == conn.sourcePort
                    && this.connection.destination.equals(conn.destination)
                    && this.connection.destinationPort == conn.destinationPort;
        }
        return false;
    }

    protected final boolean fromDestination(PcapPacket pkt) {
        Connection conn = PacketTools.getConnection(pkt);
        if (conn != null) {
            return this.connection.source.equals(conn.destination)
                    && this.connection.sourcePort == conn.destinationPort
                    && this.connection.destination.equals(conn.source)
                    && this.connection.destinationPort == conn.sourcePort;
        }
        return false;
    }

    public abstract ConnectionData createInstance(Connection connection, long timeCreatedNanoSec, boolean inbound);

}
