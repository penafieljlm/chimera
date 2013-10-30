/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JHeaderPool;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.UnregisteredHeaderException;
import ph.edu.dlsu.chimera.server.core.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduAtomic extends Pdu {

    public final PcapPacket packet;
    private Connection connection;
    private String[] instanceHeaders;
    private String[] instanceData;

    public PduAtomic(JPacket packet,
            boolean inbound) {
        super(inbound, packet.getCaptureHeader().timestampInNanos());
        this.packet = new PcapPacket(packet);
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

    public void setInstance(String[] headers, String[] data) {
        this.instanceHeaders = headers;
        this.instanceData = data;
    }

    public String[] getInstanceHeaders() {
        return this.instanceHeaders;
    }

    public String[] getInstanceData() {
        return this.instanceData;
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
