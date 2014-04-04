package ph.edu.dlsu.chimera.core;

import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JHeaderPool;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;

/**
 * An instance of this class constitutes a Pdu which spans across one network
 * packet.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduAtomic extends Pdu {

    /**
     * The packet being handled by this PduAtomic object
     */
    public final PcapPacket packet;
    private Connection connection;

    /**
     * Constructs a new PduAtomic object.
     *
     * @param packet The packet to be handled
     * @param direction The direction that the packet was traveling
     */
    public PduAtomic(PcapPacket packet,
            TrafficDirection direction) {
        super(direction, packet.getCaptureHeader().timestampInNanos());
        this.packet = packet;
        this.connection = null;
    }

    /**
     *
     * @return The protocol of the packet being handled by this PduAtomic
     * object.
     */
    public String getProtocolName() {
        if (this.packet.getHeaderCount() > 0) {
            int off = 0;
            JHeader curjh = null;
            do {
                off++;
                try {
                    int id = this.packet.getHeaderIdByIndex(this.packet.getHeaderCount() - off);
                    JHeader nextjh = JHeaderPool.getDefault().getHeader(id);
                    if (nextjh == null) {
                        break;
                    } else {
                        curjh = nextjh;
                    }
                } catch (Exception ex) {
                    break;
                }
            } while (curjh instanceof Payload);
            if (curjh != null) {
                return curjh.getName();
            }
        }
        return "Unknown";
    }

    @Override
    public long size() {
        return this.packet.size();
    }

    /**
     * Attaches a TCP Stream statistics monitor (a Connection object) to this
     * PduAtomic object. The statistics monitor can then be used by subsequent
     * modules that will handle this PduAtomic object.
     *
     * @param connection The connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     *
     * @return The TCP Stream statistics monitor (Connection object) attached to
     * this PduAtomic object.
     */
    public Connection getConnection() {
        return this.connection;
    }
}
