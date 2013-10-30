/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.constants.Defaults;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentFork;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInjector;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInstanceDumper;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInstancePreprocessor;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentGathering extends Deployment {

    public Gathering(Assembly assembly, String ifInbound) {
        super("Data Gathering");
        //inbound assembly
        ConcurrentLinkedQueue<Packet> outSnifferQueue = new ConcurrentLinkedQueue<Packet>();
        super.components.put("in.sniffer", new SnifferNetwork(assembly, outSnifferQueue, ifInbound));
    }
}
