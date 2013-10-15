/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentStateTable extends ComponentActive {

    public final ConcurrentHashMap<SocketPair, Connection> stateTable;
    public final long stateTimeoutMs;

    public ComponentStateTable(Assembly assembly,
            ConcurrentHashMap<SocketPair, Connection> stateTable,
            long stateTimeoutMs) {
        super(assembly);
        this.setPriority(Thread.MIN_PRIORITY);
        this.stateTable = stateTable;
        this.stateTimeoutMs = stateTimeoutMs;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.stateTable != null) {
                for (SocketPair socks : this.stateTable.keySet()) {
                    if (this.stateTable.get(socks).getTimeSinceLastEncounterMs() > this.stateTimeoutMs) {
                        //state timed out
                        this.stateTable.remove(socks);
                    }
                }
            }
        }
    }

    @Override
    public ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.stateTable != null) {
            for (SocketPair socks : this.stateTable.keySet()) {
                Connection connection = this.stateTable.get(socks);
                StringBuilder keybld = new StringBuilder();
                String ins;
                int insprt;
                String ext;
                int extprt;
                if (connection.inbound) {
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
                diag.add(new Diagnostic(keybld.toString(), keybld.toString(), connection.getDiagnostics()));
            }
        }
        return diag;
    }
}
