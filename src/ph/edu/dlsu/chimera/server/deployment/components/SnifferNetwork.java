/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.core.Diagnostic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class SnifferNetwork extends Sniffer {

    public final String device;

    public SnifferNetwork(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> outQueue, String device) {
        super(assembly, outQueue);
        this.device = device;
    }

    @Override
    protected Pcap open() throws Exception {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Pcap.openLive(this.device, Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, null);
        if(pcap == null || errbuf.length() > 0)
            throw new Exception("Could not open device: " + this.device);
        return pcap;
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.add(new Diagnostic("device", "Device", this.device));
        return diag;
    }

}
