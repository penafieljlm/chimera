/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class StateTableComponent extends Component {

    public final ConcurrentHashMap<Connection, ConnectionData> stateTable;

    public StateTableComponent(Assembly assembly, ConcurrentHashMap<Connection, ConnectionData> stateTable) {
        super(assembly);
        this.stateTable = stateTable;
    }

    @Override
    protected void componentRun() {
        
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        return null;
    }

}
