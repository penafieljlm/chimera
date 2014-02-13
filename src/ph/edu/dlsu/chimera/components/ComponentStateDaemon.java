/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.TcpSocketPair;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.TrafficDirection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentStateDaemon extends ComponentActive {

    public final ConcurrentHashMap<TcpSocketPair, Connection> stateTable;
    public final long stateTimeoutMs;

    public ComponentStateDaemon(ConcurrentHashMap<TcpSocketPair, Connection> stateTable, long stateTimeoutMs) {
        this.setPriority(Thread.MIN_PRIORITY);
        this.stateTable = stateTable;
        this.stateTimeoutMs = stateTimeoutMs;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.stateTable != null) {
                synchronized (this.stateTable) {
                    for (TcpSocketPair socks : this.stateTable.keySet()) {
                        if (this.stateTable.get(socks).getTimeSinceLastEncounterMs() > this.stateTimeoutMs) {
                            //state timed out
                            this.stateTable.remove(socks);
                        }
                    }
                }
            } else {
                throw new Exception("Error: [State Daemon] stateTable is null.");
            }
        }
    }

    @Override
    public ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.stateTable != null) {
            synchronized (this.stateTable) {
                ArrayList<Diagnostic> states = new ArrayList<Diagnostic>();
                for (TcpSocketPair socks : this.stateTable.keySet()) {
                    Connection connection = this.stateTable.get(socks);
                    StringBuilder keybld = new StringBuilder();
                    String ins;
                    int insprt;
                    String ext;
                    int extprt;
                    if (connection.direction == TrafficDirection.Ingress) {
                        ins = socks.destination.getHostAddress();
                        insprt = socks.destinationPort;
                        ext = socks.source.getHostAddress();
                        extprt = socks.sourcePort;
                    } else {
                        ins = socks.source.getHostAddress();
                        insprt = socks.sourcePort;
                        ext = socks.destination.getHostAddress();
                        extprt = socks.destinationPort;
                    }
                    keybld = keybld.append(ext).append(":").append(extprt).append(" <-> ").append(ins).append(":").append(insprt);
                    states.add(new Diagnostic(keybld.toString(), keybld.toString(), connection.getDiagnostics()));
                }
                diag.add(new Diagnostic("states", "State Instances", states));
            }
        }
        return diag;
    }
}
