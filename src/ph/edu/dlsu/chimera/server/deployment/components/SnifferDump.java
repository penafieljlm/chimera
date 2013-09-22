/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.capture.CaptureFileOpenException;
import net.sourceforge.jpcap.capture.PacketCapture;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.core.Diagnostic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class SnifferDump extends Sniffer {

    public final String dumpfile;

    public SnifferDump(Assembly assembly, ConcurrentLinkedQueue<Packet> outQueue, String filename) {
        super(assembly, outQueue);
        this.dumpfile = filename;
    }

    @Override
    protected void open(PacketCapture pcap) throws CaptureFileOpenException {
        pcap.openOffline(this.dumpfile);
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag =  super.getDiagnostics();
        diag.add(new Diagnostic("dumpfile", "Dump File Name", this.dumpfile));
        return diag;
    }

}
