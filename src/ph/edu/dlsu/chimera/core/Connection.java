package ph.edu.dlsu.chimera.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.util.UtilsTime;

/**
 * An instance of this class constitutes a Statistics monitor object which is
 * meant to work on TCP Streams. An instance of this class also keeps track the
 * states of a TCP stream in order to detect whether or not it should be cleaned
 * up.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Connection extends Statistics implements Serializable {

    /**
     * The pair of TCP Sockets involved in the stream being monitored
     */
    public final TcpSocketPair sockets;
    /**
     * The direction that the TCP stream being monitored was created
     */
    public final TrafficDirection direction;
    private long ingressEncounters;
    private long egressEncounters;
    private long ingressTotalSize;
    private long egressTotalSize;
    private long ingressLastEncounterNanos;
    private long egressLastEncounterNanos;
    private long ingressLastLastEncounterNanos;
    private long egressLastLastEncounterNanos;
    private boolean done;
    private byte inFin; //0 - none; 1 - fin,ack; 2 - ack
    private byte egFin; //0 - none; 1 - fin,ack; 2 - ack

    /**
     * Constructs a new Connection object.
     *
     * @param sockets The pair of TCP Sockets involved in the stream being
     * monitored
     * @param timeCreatedNanos The time which the TCP Stream object monitor was
     * created
     * @param direction The direction that the TCP stream being monitored was
     * created
     */
    public Connection(TcpSocketPair sockets, long timeCreatedNanos, TrafficDirection direction) {
        super(timeCreatedNanos);
        this.sockets = sockets;
        this.direction = direction;
        this.ingressEncounters = 0;
        this.egressEncounters = 0;
        this.ingressTotalSize = 0;
        this.egressTotalSize = 0;
        if (direction == TrafficDirection.Ingress) {
            this.ingressLastEncounterNanos = timeCreatedNanos;
            this.ingressLastLastEncounterNanos = -1;
            this.egressLastEncounterNanos = -1;
            this.egressLastLastEncounterNanos = -1;
        } else {
            this.ingressLastEncounterNanos = -1;
            this.ingressLastLastEncounterNanos = -1;
            this.egressLastEncounterNanos = timeCreatedNanos;
            this.egressLastLastEncounterNanos = -1;
        }
        this.done = false;
        this.inFin = 0;
        this.egFin = 0;
    }

    /**
     *
     * @return The number of network inbound packets encountered
     */
    public synchronized long ingressEncounters() {
        return this.ingressEncounters;
    }

    /**
     *
     * @return The number of network outbound packets encountered
     */
    public synchronized long egressEncounters() {
        return this.egressEncounters;
    }

    /**
     *
     * @return The total size of network inbound traffic in bytes
     */
    public synchronized long ingressTotalSize() {
        return this.ingressTotalSize;
    }

    /**
     *
     * @return The total size of network outbound traffic in bytes
     */
    public synchronized long egressTotalSize() {
        return this.egressTotalSize;
    }

    /**
     *
     * @return The traffic rate of network inbound traffic measured as packets
     * per second
     */
    public synchronized double ingressRatePerSec() {
        double sec = this.getTimeExistedMs() / 1000;
        return (sec > 0) ? this.ingressEncounters / sec : this.ingressEncounters;
    }

    /**
     *
     * @return The traffic rate of network outbound traffic measured as packets
     * per second
     */
    public synchronized double egressRatePerSec() {
        double sec = this.getTimeExistedMs() / 1000;
        return (sec > 0) ? this.egressEncounters / sec : this.egressEncounters;
    }

    /**
     *
     * @return The average size of network inbound traffic in bytes
     */
    public synchronized double ingressAverageSize() {
        return (this.ingressEncounters > 0) ? this.ingressTotalSize / this.ingressEncounters : this.ingressTotalSize;
    }

    /**
     *
     * @return The average size of network outbound traffic in bytes
     */
    public synchronized double egressAverageSize() {
        return (this.egressEncounters > 0) ? this.egressTotalSize / this.egressEncounters : this.egressTotalSize;
    }

    /**
     *
     * @return The last time a network inbound packet had been encountered in
     * milliseconds
     */
    public synchronized long ingressLastEncounterTimeNs() {
        return this.ingressLastEncounterNanos;
    }

    /**
     *
     * @return The last time a network outbound packet had been encountered in
     * milliseconds
     */
    public synchronized long egressLastEncounterTimeNs() {
        return this.egressLastEncounterNanos;
    }

    /**
     *
     * @return The amount of time since a network ingress packet had been
     * encountered in milliseconds
     */
    public synchronized double ingressTimeSinceLastEncounterMs() {
        return UtilsTime.nowMs() - UtilsTime.nsToMs(this.ingressLastEncounterNanos);
    }

    /**
     *
     * @return The amount of time since a network outbound packet had been
     * encountered in milliseconds
     */
    public synchronized double egressTimeSinceLastEncounterMs() {
        return UtilsTime.nowMs() - UtilsTime.nsToMs(this.egressLastEncounterNanos);
    }

    /**
     *
     * @return Time interval between the last network inbound packet encounter
     * and the network outbound packet encounter before that in nanoseconds
     */
    public synchronized long ingressLastEncounterDeltaNs() {
        return (this.ingressLastLastEncounterNanos < 0) ? -1 : this.ingressLastEncounterNanos - this.ingressLastLastEncounterNanos;
    }

    /**
     *
     * @return Time interval between the last network outbound packet encounter
     * and the network outbound packet encounter before that in nanoseconds
     */
    public synchronized long egressLastEncounterDeltaNs() {
        return (this.egressLastLastEncounterNanos < 0) ? -1 : this.egressLastEncounterNanos - this.egressLastLastEncounterNanos;
    }

    /**
     * Updates the monitor values based on the received packet.
     *
     * @param pkt The received packet.
     */
    public synchronized void update(PduAtomic pkt) {
        if (!this.done) {
            Tcp tcp = pkt.packet.getHeader(new Tcp());
            super.commitEncounter(pkt.timestampInNanos, pkt.size());
            if (pkt.direction == TrafficDirection.Ingress) {
                this.ingressEncounters++;
                this.ingressTotalSize += pkt.packet.size();
                this.ingressLastLastEncounterNanos = this.ingressLastEncounterNanos;
                this.ingressLastEncounterNanos = pkt.packet.getCaptureHeader().timestampInNanos();
                if (tcp.flags_ACK() && this.egFin == 1) {
                    this.egFin = 2;
                }
                if (tcp.flags_FIN() && this.inFin == 0) {
                    this.inFin = 1;
                }
            } else {
                this.egressEncounters++;
                this.egressTotalSize += pkt.packet.size();
                this.egressLastLastEncounterNanos = this.egressLastEncounterNanos;
                this.egressLastEncounterNanos = pkt.packet.getCaptureHeader().timestampInNanos();
                if (tcp.flags_ACK() && this.inFin == 1) {
                    this.inFin = 2;
                }
                if (tcp.flags_FIN() && this.egFin == 0) {
                    this.egFin = 1;
                }
            }
            if (tcp.flags_RST() || (this.inFin == 2 && this.egFin == 2)) {
                this.done = true;
            }
        }
    }

    /**
     *
     * @return True if the TCP stream being monitored has finished.
     */
    public synchronized boolean isDone() {
        return this.done;
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        Date inencounter = (this.ingressLastEncounterNanos < 0) ? null : new java.sql.Date(UtilsTime.nsToMs(this.ingressLastEncounterNanos));
        Date egencounter = (this.egressLastEncounterNanos < 0) ? null : new java.sql.Date(UtilsTime.nsToMs(this.egressLastEncounterNanos));
        diag.add(new Diagnostic("direction", "Direction", (this.direction == TrafficDirection.Ingress) ? "ingress" : "egress"));
        diag.add(new Diagnostic("ingressct", "Ingress Packets Encountered", this.ingressEncounters));
        diag.add(new Diagnostic("egressct", "Egress Packets Encountered", this.egressEncounters));
        diag.add(new Diagnostic("ingresssize", "Ingress Traffic Total Size", this.ingressTotalSize));
        diag.add(new Diagnostic("egresssize", "Egress Traffic Total Size", this.egressTotalSize));
        diag.add(new Diagnostic("inavgsize", "Ingress Traffic Average Size", this.ingressAverageSize()));
        diag.add(new Diagnostic("egavgsize", "Egress Traffic Average Size", this.egressAverageSize()));
        diag.add(new Diagnostic("inrate", "Ingress Traffic Rate", this.ingressRatePerSec() + "pkts/sec"));
        diag.add(new Diagnostic("egrate", "Egress Traffic Rate", this.egressRatePerSec() + "pkts/sec"));
        diag.add(new Diagnostic("inlastencounter", "Ingress Last Encounter", (inencounter == null) ? "N/A" : inencounter.toLocaleString()));
        diag.add(new Diagnostic("eglastencounter", "Egress Last Encounter", (egencounter == null) ? "N/A" : egencounter.toLocaleString()));
        diag.add(new Diagnostic("inidletime", "Ingress Idle Time", (this.ingressLastEncounterNanos < 0) ? "N/A" : this.ingressTimeSinceLastEncounterMs() + "ms"));
        diag.add(new Diagnostic("egideltime", "Egress Idle Time", (this.egressLastEncounterNanos < 0) ? "N/A" : this.egressTimeSinceLastEncounterMs() + "ms"));
        diag.add(new Diagnostic("done", "Connection Finished", this.done));
        return diag;
    }
}
