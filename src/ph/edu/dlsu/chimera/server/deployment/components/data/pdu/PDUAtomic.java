/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.util.ToolsTime;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduAtomic extends Pdu {

    public final Pcap sniffer;
    public final PcapPacket packet;
    private Pcap injector;
    private long lastSentTimeNano;
    private Connection connection;
    private String[] instanceData;

    public PduAtomic(Pcap sourceInjector,
            PcapPacket packet,
            boolean inbound,
            long timestampInNanos) {
        super(inbound, timestampInNanos);
        this.sniffer = sourceInjector;
        this.packet = packet;
        this.injector = null;
        this.lastSentTimeNano = -1;
        this.connection = null;
    }

    public void setInstanceData(String[] instanceData) {
        this.instanceData = instanceData;
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

    public Pcap getDestinationInjector() {
        return this.injector;
    }

    /**
     * Notify this packet that it has been sent
     */
    public synchronized void notifySent(Pcap sender) {
        this.injector = sender;
        this.lastSentTimeNano = ToolsTime.nowNs();
    }

    /**
     * @return last time this packet was sent; -1 if never sent.
     */
    public synchronized long getLastSentTimeNanos() {
        return this.lastSentTimeNano;
    }

    public synchronized long getTimeSinceSentNanos() {
        return ToolsTime.nowNs() - this.lastSentTimeNano;
    }
}
