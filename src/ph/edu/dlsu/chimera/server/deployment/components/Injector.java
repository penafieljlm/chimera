/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Injector extends Component {

    protected final ConcurrentLinkedQueue<PcapPacket> inQueue;

    public Injector(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue) {
        super(assembly);
        this.inQueue = inQueue;
    }

    @Override
    protected void componentRun() {
        
    }
    
}
