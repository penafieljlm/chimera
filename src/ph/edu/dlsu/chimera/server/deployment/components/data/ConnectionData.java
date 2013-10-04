/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.IDiagnosable;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ConnectionData implements IDiagnosable {

    public final long timeCreatedNanos; //nano
    public final boolean inbound;
    private int inboundEncounters;
    private int outboundEncounters;
    private boolean done;
    private byte inFin; //0 - none; 1 - fin,ack; 2 - ack
    private byte outFin; //0 - none; 1 - fin,ack; 2 - ack

    public ConnectionData(long timeCreatedNanos, boolean inbound) {
        this.timeCreatedNanos = timeCreatedNanos;
        this.inbound = inbound;
        this.inboundEncounters = 0;
        this.outboundEncounters = 0;
        this.done = false;
        this.inFin = 0;
        this.outFin = 0;
    }

    /**
     * @return the length of time this TCPState has existed in milliseconds.
     */
    public synchronized long getStateTime() {
        Date now = new Date();
        return now.getTime() - (this.timeCreatedNanos / 1000000);
    }

    /**
     * @return the inbound rate of this TCPState measured as packet per second.
     */
    public synchronized double inboundRate() {
        double sec = this.getStateTime() / 1000;
        return this.inboundEncounters / sec;
    }

    /**
     * @return the outbound rate of this TCPState measured as packet per second.
     */
    public synchronized double outboundRate() {
        double sec = this.getStateTime() / 1000;
        return this.outboundEncounters / sec;
    }

    /**
     * Updates the connection data based on the received packet.
     * @param pkt - the received packet.
     */
    public synchronized void update(PcapPacket pkt, boolean inbound) {
        if (!this.done) {
            Tcp tcp = pkt.getHeader(new Tcp());
            if (inbound) {
                this.inboundEncounters++;
                if(tcp.flags_ACK() && this.outFin == 1) {
                    this.outFin = 2;
                }
                if (tcp.flags_FIN() && this.inFin == 0) {
                    this.inFin = 1;
                }
            } else {
                this.outboundEncounters++;
                if(tcp.flags_ACK() && this.inFin == 1) {
                    this.inFin = 2;
                }
                if (tcp.flags_FIN() && this.outFin == 0) {
                    this.outFin = 1;
                }
            }
            if (tcp.flags_RST() || (this.inFin == 2 && this.outFin == 2)) {
                this.done = true;
            }
        }
    }

    public synchronized boolean isDone() {
        return this.done;
    }

    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = new ArrayList<Diagnostic>();
        Date create = new java.sql.Date(this.timeCreatedNanos / 1000000);
        diag.add(new Diagnostic("createtime", "Time Created", create.toLocaleString()));
        diag.add(new Diagnostic("activetime", "Time Active", this.getStateTime() + "ms"));
        diag.add(new Diagnostic("direction", "Direction", (this.inbound) ? "inbound" : "outbound"));
        diag.add(new Diagnostic("inbound", "Inbound Packets Encountered", this.inboundEncounters));
        diag.add(new Diagnostic("outbound", "Outbound Packets Encountered", this.outboundEncounters));
        diag.add(new Diagnostic("inrate", "Inbound Traffic Rate", this.inboundRate()));
        diag.add(new Diagnostic("outrate", "Outbound Traffic Rate", this.outboundRate()));
        diag.add(new Diagnostic("done", "Connection Finished", this.done));
        return diag;
    }

}
