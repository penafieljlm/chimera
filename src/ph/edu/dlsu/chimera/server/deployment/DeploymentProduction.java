/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import ph.edu.dlsu.chimera.server.Assembly;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentProduction extends Deployment {

    public Production(Assembly assembly, String ifInbound) {
        super("Production");
        ConcurrentLinkedQueue<Packet> inSnifferQueue = new ConcurrentLinkedQueue<Packet>();
        super.components.put("in-sniffer", new SnifferNetwork(assembly, inSnifferQueue, ifInbound));
    }

}
