package ph.edu.dlsu.chimera.server;

import ph.edu.dlsu.chimera.core.Diagnostic;
import java.util.ArrayList;

/**
 * An instance of this class constitutes a Component which runs on its own separate Thread.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Component extends Thread {

    /**
     * The assembly which this component is a member of.
     */
    public final Assembly assembly;
    /**
     * Flags whether or not the Component object is running.
     */
    protected boolean running;

    /**
     * Constructs a new Component object.
     * @param assembly - the assembly which this component is a member of.
     */
    public Component(Assembly assembly) {
        this.assembly = assembly;
        this.running = false;
    }

    /**
     * Kills the component's thread.
     */
    public synchronized void kill() {
        this.running = false;
    }

    /**
     * @return a report on the current state of the Component object.
     */
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = new ArrayList<Diagnostic>();
        diag.add(new Diagnostic("running", "Is Running", this.running));
        return diag;
    }

    /**
     * Set running flags when component is started.
     */
    @Override
    public void run() {
        synchronized(this) {
            this.running = true;
        }
        this.componentRun();
        synchronized(this) {
            this.running = false;
        }
    }

    /**
     * The task of the component.
     */
    protected abstract void componentRun();

}
