/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Injector extends Component {

    protected final ConcurrentLinkedQueue<Packet> inQueue;

    public Injector(Assembly assembly, ConcurrentLinkedQueue<Packet> inQueue) {
        super(assembly);
        this.inQueue = inQueue;
    }

    @Override
    protected void componentRun() {
        
    }
    
}
