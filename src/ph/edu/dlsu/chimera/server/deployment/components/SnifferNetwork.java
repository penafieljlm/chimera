/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.capture.PacketCapture;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.core.Diagnostic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class SnifferNetwork extends Sniffer {

    public final String device;

    public SnifferNetwork(Assembly assembly, ConcurrentLinkedQueue<Packet> outQueue, String device) {
        super(assembly, outQueue);
        this.device = device;
    }

    @Override
    protected void open(PacketCapture pcap) throws Exception {
        pcap.open(this.device, true);
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.add(new Diagnostic("device", "Device", this.device));
        return diag;
    }

}
