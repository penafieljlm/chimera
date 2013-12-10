/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.assembler.Assembler;
import ph.edu.dlsu.chimera.core.TcpSocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentAssemblerTable extends ComponentActive {

    public final ConcurrentHashMap<TcpSocketPair, Assembler> assemblertable;

    public ComponentAssemblerTable(ConcurrentHashMap<TcpSocketPair, Assembler> assemblertable) {
        this.assemblertable = assemblertable;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.assemblertable != null) {
                synchronized (this.assemblertable) {
                    for (TcpSocketPair socks : this.assemblertable.keySet()) {
                        if (this.assemblertable.get(socks).isAttackDetected()) {
                            //attack detected
                            this.assemblertable.remove(socks);
                        }
                    }
                }
            } else {
                throw new Exception("Error: [Assembler Table] statisticsTable is null.");
            }
        }
    }

    public ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.assemblertable != null) {
            synchronized (this.assemblertable) {
                ArrayList<Diagnostic> states = new ArrayList<>();
                for (TcpSocketPair socks : this.assemblertable.keySet()) {
                    Assembler assembler = this.assemblertable.get(socks);
                    StringBuilder keybld = new StringBuilder();
                    String ins = socks.destination.getHostAddress();
                    int insprt = socks.destinationPort;
                    String ext = socks.source.getHostAddress();
                    int extprt = socks.sourcePort;
                    keybld = keybld.append(ext).append(":").append(extprt).append(" <-> ").append(ins).append(":").append(insprt);
                    states.add(new Diagnostic(keybld.toString(), keybld.toString(), assembler.getDiagnostics()));
                }
                diag.add(new Diagnostic("assemblers", "Assembler Instances", states));
            }
        }
        return diag;
    }
}
