/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Date;
import ph.edu.dlsu.chimera.server.deployment.components.handler.ProtocolHandler;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConnectionData {

    public final Connection connection;
    public final long timeCreated;
    public final ProtocolHandler inboundHandler;
    protected int inboundEncounters;
    protected int outboundEncounters;

    public ConnectionData(Connection connection, long timeCreated, ProtocolHandler inboundHandler) {
        this.connection = connection;
        this.timeCreated = timeCreated;
        this.inboundHandler = inboundHandler;
        this.inboundEncounters = 0;
        this.outboundEncounters = 0;
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
     * @param inbound - true if the packet was received on the interface facing outside.
     */
    public void update(boolean inbound) {
        if(inbound)
            this.inboundEncounters++;
        else
            this.outboundEncounters++;
    }

}
