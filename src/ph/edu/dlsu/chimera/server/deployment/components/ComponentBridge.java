/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import com.gremwell.jnetbridge.PcapPort;
import com.gremwell.jnetbridge.QueueingPortListener;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;

/**
 *
 * @author User
 */
public class ComponentBridge extends ComponentActive {

    public final PcapPort inPcapPort;
    public final PcapPort outPcapPort;
    private long forwarded;

    public ComponentBridge(Assembly assembly, PcapPort inPcapPort, PcapPort outPcapPort) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inPcapPort = inPcapPort;
        this.outPcapPort = outPcapPort;
        this.forwarded = 0;
    }

    @Override
    public void componentRun() throws Exception {
        if (this.inPcapPort == null) {
            throw new Exception("Error: [Bridge] Unable to access capture device.");
        }
        if (this.outPcapPort == null) {
            throw new Exception("Error: [Bridge] Unable to access sending device.");
        }
        QueueingPortListener inQueue = new QueueingPortListener();
        this.inPcapPort.setListener(inQueue);
        this.inPcapPort.start();
        this.outPcapPort.start();
        while (super.running) {
            this.outPcapPort.send(inQueue.receive().packet);
            this.forwarded++;
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.add(new Diagnostic("forwarded", "Packets Forwarded", this.forwarded));
        return diag;
    }
}
