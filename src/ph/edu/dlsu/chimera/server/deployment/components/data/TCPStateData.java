/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Date;
import net.sourceforge.jpcap.util.Timeval;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class TCPStateData {

    public final Date timeCreated;
    private int inboundEncounters;
    private int outboundEncounters;

    public TCPStateData(Timeval timeCreated) {
        this.timeCreated = timeCreated.getDate();
        this.inboundEncounters = 0;
        this.outboundEncounters = 0;
    }

    /**
     * Decrements the inbound packet encounter counter.
     */
    public void inboundIncrement() {
        this.inboundEncounters++;
    }

    /**
     * Increments the outbound packet encounter counter.
     */
    public void outboundIncrement() {
        this.outboundEncounters++;
    }

    /**
     * @return the length of time this TCPState has existed in microseconds.
     */
    public long getStateTime() {
        Date now = new Date();
        return now.getTime() - this.timeCreated.getTime();
    }

    /**
     * @return the inbound rate of this TCPState measured as packet per second.
     */
    public double inboundRate() {
        double sec = this.getStateTime() / 1000000;
        return this.inboundEncounters / sec;
    }

    /**
     * @return the outbound rate of this TCPState measured as packet per second.
     */
    public double outboundRate() {
        double sec = this.getStateTime() / 1000000;
        return this.outboundEncounters / sec;
    }

}
