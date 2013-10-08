/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentStateTable implements Component {

    private final ConcurrentHashMap<Connection, ConnectionData> stateTable;

    public ComponentStateTable(ConcurrentHashMap<Connection, ConnectionData> stateTable) {
        this.stateTable = stateTable;
    }

    public ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = new ArrayList<Diagnostic>();
        if (this.stateTable != null) {
            for (Connection conn : this.stateTable.keySet()) {
                ConnectionData conndat = this.stateTable.get(conn);
                StringBuilder keybld = new StringBuilder();
                String ins;
                int insprt;
                String ext;
                int extprt;
                if (conndat.inbound) {
                    ins = conn.destination.getHostAddress();
                    insprt = conn.destinationPort;
                    ext = conn.source.getHostAddress();
                    extprt = conn.sourcePort;
                } else {
                    ins = conn.source.getHostAddress();
                    insprt = conn.sourcePort;
                    ext = conn.destination.getHostAddress();
                    extprt = conn.destinationPort;
                }
                keybld = keybld.append(ext).append(":").append(extprt).append(" <-> ").append(ins).append(":").append(insprt);
                diag.add(new Diagnostic(keybld.toString(), keybld.toString(), conndat.getDiagnostics()));
            }
        }
        return diag;
    }
}
