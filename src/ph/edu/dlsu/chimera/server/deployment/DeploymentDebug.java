/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentAssembler;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentDebugger;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcpHttp;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Statistics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentDebug extends Deployment {

    public DeploymentDebug(Assembly assembly, String ifInbound, List<Criteria> criterias, long statsTimeoutMs, long stateTimeoutMs) {
        super("Debug");
        StringBuilder err = new StringBuilder();
        Pcap inPcap = Pcap.openLive(ifInbound, Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, err);
        ConcurrentLinkedQueue<PduAtomic> snifferOut = new ConcurrentLinkedQueue<PduAtomic>();
        ConcurrentLinkedQueue<PduAtomic> statsOut = new ConcurrentLinkedQueue<PduAtomic>();
        ConcurrentLinkedQueue<PduAtomic> stateOut = new ConcurrentLinkedQueue<PduAtomic>();
        ConcurrentLinkedQueue<Pdu> assemblerOut = new ConcurrentLinkedQueue<Pdu>();
        ConcurrentHashMap<Criteria, Statistics> statsTable = new ConcurrentHashMap<Criteria, Statistics>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<SocketPair, Connection>();
        ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup = new ConcurrentHashMap<Integer, AssemblerTcp>();
        tcpPortProtocolLookup.put(80, new AssemblerTcpHttp());
        super.components.put("statstable", new ComponentStatisticsTable(assembly, criterias, statsTable, statsTimeoutMs));
        super.components.put("statetable", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));
        super.components.put("in-sniffer", new ComponentSniffer(assembly, inPcap, snifferOut, true));
        super.components.put("in-statstracker", new ComponentStatisticsTracker(assembly, stateOut, statsOut, criterias, statsTable));
        super.components.put("in-statetracker", new ComponentStateTracker(assembly, statsOut, stateOut, stateTable, true));
        super.components.put("in-assembler", new ComponentAssembler(assembly, stateOut, assemblerOut, tcpPortProtocolLookup, null, stateTable));
        super.components.put("in-debugger", new ComponentDebugger(assembly, assemblerOut, null));
    }
}
