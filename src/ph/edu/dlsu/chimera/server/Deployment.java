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
public abstract class Deployment {

    public final String name;

    protected final HashMap<String, Component> components;

    public Deployment(String name) {
        this.name = name;
        this.components = new HashMap<String, Component>();
    }

    public ArrayList<Diagnostic> getDiagnostics(String componentName) {
        return this.components.get(componentName).getDiagnostics();
    }

    public synchronized void killDeployment() {
        for(String k : this.components.keySet()) {
            if(this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.kill();
            }
        }
    }

    protected void startDeployment() {
        for(String k : this.components.keySet()) {
            if(this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.start();
            }
        }
    }

}
