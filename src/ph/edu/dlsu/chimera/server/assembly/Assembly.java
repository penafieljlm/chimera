/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly;

import java.util.HashMap;
import java.util.Set;
import ph.edu.dlsu.chimera.server.assembly.components.Component;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentActive;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentController;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembly {

    public final String name;
    protected boolean isRunning;
    private final HashMap<String, Component> components;
    private final ComponentController controller;

    public Assembly(String name, int controlPort) {
        this.name = name;
        this.isRunning = false;
        this.components = new HashMap<>();
        this.controller = new ComponentController(this, controlPort);
    }

    public Set<String> getComponentNames() {
        return this.components.keySet();
    }

    public Component getComponent(String componentName) throws Exception {
        Component component = this.components.get(componentName);
        if(component == null)
            throw new Exception("No such component: " + componentName + "!");
        return component;
    }

    public synchronized void kill() {
        for (String k : this.components.keySet()) {
            if (this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.kill();
            }
        }
        this.controller.kill();
        this.isRunning = false;
    }

    public synchronized void run() throws InterruptedException {
        this.isRunning = true;
        for (String k : this.components.keySet()) {
            if (this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.start();
            }
        }
        this.controller.start();
        for (String k : this.components.keySet()) {
            if (this.components.get(k) instanceof ComponentActive) {
                ComponentActive ca = (ComponentActive) this.components.get(k);
                ca.join();
            }
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
    
    protected Component addComponent(String name, Component component) {
        if(component instanceof ComponentActive) {
            ComponentActive ca = (ComponentActive) component;
            ca.setName(name);
        }
        return this.components.put(name, component);
    }
}
