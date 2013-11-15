/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.pdu;

import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JHeaderPool;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.core.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduAtomic extends Pdu {

    public final PcapPacket packet;
    private Connection connection;

    public PduAtomic(PcapPacket packet,
            boolean inbound) {
        super(inbound, packet.getCaptureHeader().timestampInNanos());
        this.packet = packet;
        this.connection = null;
    }

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

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
