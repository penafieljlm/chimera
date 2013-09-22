/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Deployment;
import ph.edu.dlsu.chimera.server.deployment.components.SnifferDump;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Training extends Deployment {

    public Training(Assembly assembly, String inFile) {
        super(assembly);
        super.setName("Training");
        ConcurrentLinkedQueue<Packet> inSnifferQueue = new ConcurrentLinkedQueue<Packet>();
        super.components.put("in-sniffer", new SnifferDump(assembly, inSnifferQueue, inFile));
    }

}
