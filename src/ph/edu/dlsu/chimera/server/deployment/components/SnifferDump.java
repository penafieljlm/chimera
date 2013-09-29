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
public class SnifferDump extends Sniffer {

    public final String dumpfile;

    public SnifferDump(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> outQueue, String filename) {
        super(assembly, outQueue);
        this.dumpfile = filename;
    }

    @Override
    protected Pcap open() throws Exception {
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Pcap.openOffline(this.dumpfile, errbuf);
        if(pcap == null || errbuf.length() > 0)
            throw new Exception("Could not open file: " + this.dumpfile);
        return pcap;
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag =  super.getDiagnostics();
        diag.add(new Diagnostic("dumpfile", "Dump File Name", this.dumpfile));
        return diag;
    }

}
