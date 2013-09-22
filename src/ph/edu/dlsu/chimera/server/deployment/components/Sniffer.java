/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jpcap.capture.PacketCapture;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.core.Diagnostic;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Sniffer extends Component implements PacketListener {

    
    private final ConcurrentLinkedQueue<Packet> outQueue;
    private final PacketCapture pcap;

    public Sniffer(Assembly assembly, ConcurrentLinkedQueue<Packet> outQueue) {
        super(assembly);
        this.outQueue = outQueue;
        this.pcap = new PacketCapture();
    }

    @Override
    public void componentRun() {
        try {
            this.pcap.addPacketListener(this);
            this.open(this.pcap);
            this.pcap.capture(-1);
            this.pcap.close();
        } catch (Exception ex) {
            Logger.getLogger(Sniffer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void packetArrived(Packet packet) {
        this.outQueue.add(packet);
    }

    @Override
    public synchronized void kill() {
        super.kill();
        this.pcap.endCapture();
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        diag.add(new Diagnostic("received", "Packets Received", this.pcap.getStatistics().getReceivedCount()));
        diag.add(new Diagnostic("dropped", "Packets Dropped", this.pcap.getStatistics().getDroppedCount()));
        return diag;
    }

    protected abstract void open(PacketCapture pcap) throws Exception;

}
