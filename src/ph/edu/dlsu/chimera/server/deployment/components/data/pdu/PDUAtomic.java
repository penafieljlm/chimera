/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.Date;
import java.util.HashMap;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.atomic.CriteriaAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduAtomic extends Pdu {

    public final Pcap sniffer;
    public final PcapPacket packet;
    private Pcap injector;
    private long lastSentTimeNano;
    private HashMap<CriteriaAtomic, Statistics> statistics; //type, statistics
    private Connection connection;

    public PduAtomic(Pcap sourceInjector,
            PcapPacket packet,
            boolean inbound) {
        super(inbound);
        this.sniffer = sourceInjector;
        this.packet = packet;
        this.injector = null;
        this.lastSentTimeNano = -1;
        this.statistics = new HashMap<CriteriaAtomic, Statistics>();
        this.connection = null;
    }

    @Override
    public long size() {
        return this.packet.size();
    }

    @Override
    public long timestampInNanos() {
        return this.packet.getCaptureHeader().timestampInNanos();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void addStatistics(CriteriaAtomic criteria, Statistics statistics) {
        for (CriteriaAtomic crt : this.statistics.keySet()) {
            if (criteria.getClass() == crt.getClass()) {
                return;
            }
        }
        this.statistics.put(criteria, statistics);
    }

    public Statistics getStatisticsByType(CriteriaAtomic criteria) {
        for (CriteriaAtomic crt : this.statistics.keySet()) {
            if (criteria.getClass() == crt.getClass()) {
                return this.statistics.get(crt);
            }
        }
        return null;
    }

    public Statistics getStatistics(CriteriaAtomic criteria) {
        return this.statistics.get(criteria);
    }

    public Pcap getDestinationInjector() {
        return this.injector;
    }

    /**
     * Notify this packet that it has been sent
     */
    public synchronized void notifySent(Pcap sender) {
        this.injector = sender;
        Date now = new Date();
        this.lastSentTimeNano = now.getTime() * 1000000;
    }

    /**
     * @return last time this packet was sent; -1 if never sent.
     */
    public synchronized long getLastSentTime() {
        return this.lastSentTimeNano;
    }

    public synchronized long getTimeSinceSent() {
        Date now = new Date();
        long lnow = now.getTime() * 1000000;
        return lnow - this.lastSentTimeNano;
    }
}
