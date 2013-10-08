/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.Date;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInjector;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUAtomic extends PDU {

    public final Pcap sniffer;
    public final PcapPacket packet;
    public final boolean inbound;
    private Pcap injector;
    private long lastSentTimeNano;

    public PDUAtomic(Pcap sourceInjector, PcapPacket packet, boolean inbound) {
        this.sniffer = sourceInjector;
        this.packet = packet;
        this.inbound = inbound;
        this.injector = null;
        this.lastSentTimeNano = -1;
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
