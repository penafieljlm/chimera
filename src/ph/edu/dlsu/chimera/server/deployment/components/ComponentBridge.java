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

    public final QueueingPortListener inQueue;
    public final PcapPort outPcapPort;
    private long forwarded;

    public ComponentBridge(Assembly assembly, PcapPort inPcapPort, PcapPort outPcapPort) {
        super(assembly);
        this.inQueue = new QueueingPortListener();
        inPcapPort.setListener(this.inQueue);
        this.outPcapPort = outPcapPort;
        this.forwarded = 0;
    }

    @Override
    public void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                if (this.outPcapPort != null) {
                    this.outPcapPort.send(this.inQueue.receive().packet);
                    this.forwarded++;
                }
            } else {
                throw new Exception("Error: [Bridge] inQueue is null.");
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.add(new Diagnostic("forwarded", "Packets Forwarded", this.forwarded));
        return diag;
    }
}
