/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server;

import ph.edu.dlsu.chimera.core.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Deployment extends Component {

    protected final HashMap<String, Component> components;

    public Deployment(Assembly assembly) {
        super(assembly);
        this.components = new HashMap<String, Component>();
    }

    public ArrayList<Diagnostic> getDiagnostics(String componentName) {
        return this.components.get(componentName).getDiagnostics();
    }

    @Override
    public synchronized void kill() {
        for(String k : this.components.keySet()) {
            this.components.get(k).kill();
        }
        super.kill();
    }

    @Override
    protected void componentRun() {
        for(String k : this.components.keySet()) {
            this.components.get(k).start();
        }
        while(super.running) {
        }
    }

}
