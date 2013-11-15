/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.util.ArrayList;
import java.util.Date;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.TimeUtils;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Connection extends Statistics {

    public final SocketPair sockets;
    public final boolean inbound;
    private long inboundEncounters;
    private long outboundEncounters;
    private long inboundTotalSize;
    private long outboundTotalSize;
    private long inboundLastEncounterNanos;
    private long outboundLastEncounterNanos;
    private long inboundLastLastEncounterNanos;
    private long outboundLastLastEncounterNanos;
    private boolean done;
    private byte inFin; //0 - none; 1 - fin,ack; 2 - ack
    private byte outFin; //0 - none; 1 - fin,ack; 2 - ack

    public Connection(SocketPair sockets, long timeCreatedNanos, boolean inbound) {
        super(timeCreatedNanos);
        this.sockets = sockets;
        this.inbound = inbound;
        this.inboundEncounters = 0;
        this.outboundEncounters = 0;
        this.inboundTotalSize = 0;
        this.outboundTotalSize = 0;
        if (inbound) {
            this.inboundLastEncounterNanos = timeCreatedNanos;
            this.inboundLastLastEncounterNanos = -1;
            this.outboundLastEncounterNanos = -1;
            this.outboundLastLastEncounterNanos = -1;
        } else {
            this.inboundLastEncounterNanos = -1;
            this.inboundLastLastEncounterNanos = -1;
            this.outboundLastEncounterNanos = timeCreatedNanos;
            this.outboundLastLastEncounterNanos = -1;
        }
        this.done = false;
        this.inFin = 0;
        this.outFin = 0;
    }

    public synchronized long inboundEncounters() {
        return this.inboundEncounters;
    }

    public synchronized long outboundEncounters() {
        return this.outboundEncounters;
    }

    public synchronized long inboundTotalSize() {
        return this.inboundTotalSize;
    }

    public synchronized long outboundTotalSize() {
        return this.outboundTotalSize;
    }

    /**
     * @return the inbound rate of this Connection measured as packet per second.
     */
    public synchronized double inboundRatePerSec() {
        double sec = this.getTimeExistedMs() / 1000;
        return (sec > 0) ? this.inboundEncounters / sec : this.inboundEncounters;
    }

    /**
     * @return the outbound rate of this Connection measured as packet per second.
     */
    public synchronized double outboundRatePerSec() {
        double sec = this.getTimeExistedMs() / 1000;
        return (sec > 0) ? this.outboundEncounters / sec : this.outboundEncounters;
    }

    /**
     * @return the average size of inbound traffic
     */
    public synchronized double inboundAverageSize() {
        return (this.inboundEncounters > 0) ? this.inboundTotalSize / this.inboundEncounters : this.inboundTotalSize;
    }

    /**
     * @return the average size of outbound traffic
     */
    public synchronized double outboundAverageSize() {
        return (this.outboundEncounters > 0) ? this.outboundTotalSize / this.outboundEncounters : this.outboundTotalSize;
    }

    public synchronized long inboundLastEncounterTimeNs() {
        return this.inboundLastEncounterNanos;
    }

    public synchronized long outboundLastEncounterTimeNs() {
        return this.outboundLastEncounterNanos;
    }

    public synchronized double inboundTimeSinceLastEncounterMs() {
        return TimeUtils.nowMs() - TimeUtils.nsToMs(this.inboundLastEncounterNanos);
    }

    public synchronized double outboundTimeSinceLastEncounterMs() {
        return TimeUtils.nowMs() - TimeUtils.nsToMs(this.outboundLastEncounterNanos);
    }

    public synchronized long inboundLastEncounterDeltaNs() {
        return (this.inboundLastLastEncounterNanos < 0) ? -1 : this.inboundLastEncounterNanos - this.inboundLastLastEncounterNanos;
    }

    public synchronized long outboundLastEncounterDeltaNs() {
        return (this.outboundLastLastEncounterNanos < 0) ? -1 : this.outboundLastEncounterNanos - this.outboundLastLastEncounterNanos;
    }

    /**
     * Updates the connection data based on the received packet.
     * @param pkt - the received packet.
     */
    public synchronized void update(PduAtomic pkt) {
        if (!this.done) {
            Tcp tcp = pkt.packet.getHeader(new Tcp());
            super.commitEncounter(pkt);
            if (pkt.inbound) {
                this.inboundEncounters++;
                this.inboundTotalSize += pkt.packet.size();
                this.inboundLastLastEncounterNanos = this.inboundLastEncounterNanos;
                this.inboundLastEncounterNanos = pkt.packet.getCaptureHeader().timestampInNanos();
                if (tcp.flags_ACK() && this.outFin == 1) {
                    this.outFin = 2;
                }
                if (tcp.flags_FIN() && this.inFin == 0) {
                    this.inFin = 1;
                }
            } else {
                this.outboundEncounters++;
                this.outboundTotalSize += pkt.packet.size();
                this.outboundLastLastEncounterNanos = this.outboundLastEncounterNanos;
                this.outboundLastEncounterNanos = pkt.packet.getCaptureHeader().timestampInNanos();
                if (tcp.flags_ACK() && this.inFin == 1) {
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

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        Date inencounter = (this.inboundLastEncounterNanos < 0) ? null : new java.sql.Date(TimeUtils.nsToMs(this.inboundLastEncounterNanos));
        Date outencounter = (this.outboundLastEncounterNanos < 0) ? null : new java.sql.Date(TimeUtils.nsToMs(this.outboundLastEncounterNanos));
        diag.add(new Diagnostic("direction", "Direction", (this.inbound) ? "inbound" : "outbound"));
        diag.add(new Diagnostic("inboundct", "Inbound Packets Encountered", this.inboundEncounters));
        diag.add(new Diagnostic("outboundct", "Outbound Packets Encountered", this.outboundEncounters));
        diag.add(new Diagnostic("inboundsize", "Inbound Traffic Total Size", this.inboundTotalSize));
        diag.add(new Diagnostic("outboundsize", "Outbound Traffic Total Size", this.outboundTotalSize));
        diag.add(new Diagnostic("inavgsize", "Inbound Traffic Average Size", this.inboundAverageSize()));
        diag.add(new Diagnostic("outavgsize", "Outbound Traffic Average Size", this.outboundAverageSize()));
        diag.add(new Diagnostic("inrate", "Inbound Traffic Rate", this.inboundRatePerSec() + "pkts/sec"));
        diag.add(new Diagnostic("outrate", "Outbound Traffic Rate", this.outboundRatePerSec() + "pkts/sec"));
        diag.add(new Diagnostic("inlastencounter", "Inbound Last Encounter", (inencounter == null) ? "N/A" : inencounter.toLocaleString()));
        diag.add(new Diagnostic("outlastencounter", "Outbound Last Encounter", (outencounter == null) ? "N/A" : outencounter.toLocaleString()));
        diag.add(new Diagnostic("inidletime", "Inbound Idle Time", (this.inboundLastEncounterNanos < 0) ? "N/A" : this.inboundTimeSinceLastEncounterMs() + "ms"));
        diag.add(new Diagnostic("outideltime", "Outbound Idle Time", (this.outboundLastEncounterNanos < 0) ? "N/A" : this.outboundTimeSinceLastEncounterMs() + "ms"));
        diag.add(new Diagnostic("done", "Connection Finished", this.done));
        return diag;
    }
}
