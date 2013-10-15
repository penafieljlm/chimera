/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import ph.edu.dlsu.chimera.core.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import ph.edu.dlsu.chimera.server.IDiagnosable;
import ph.edu.dlsu.chimera.server.deployment.components.Component;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentActive;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Deployment {

    public final String name;
    protected final HashMap<String, Component> components;
    protected boolean isRunning;

    public Deployment(String name) {
        this.name = name;
        this.components = new HashMap<String, Component>();
        this.isRunning = false;
    }

    public Component getComponent(String componentName) throws Exception {
        Component component = this.components.get(componentName);
        if(component == null)
            throw new Exception("No such component: " + componentName + "!");
        return component;
    }

    public synchronized void killDeployment() {
        for (String k : this.components.keySet()) {
            if (this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.kill();
            }
        }
        this.isRunning = false;
    }

    public void startDeployment() {
        this.isRunning = true;
        for (String k : this.components.keySet()) {
            if (this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.start();
            }
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
