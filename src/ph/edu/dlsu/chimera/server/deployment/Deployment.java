/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import java.util.HashMap;
import java.util.Set;
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
    }

    public Set<String> getComponentNames() {
        return this.components.keySet();
    }

    public synchronized void killDeployment() {
        for(String k : this.components.keySet()) {
            this.components.get(k).kill();
        }
    }

    protected void startDeployment() {
        for(String k : this.components.keySet()) {
            this.components.get(k).start();
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
