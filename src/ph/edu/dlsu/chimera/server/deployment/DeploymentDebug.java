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
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTableAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTrackerAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcpHttp;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.atomic.CriteriaAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentDebug extends Deployment {

    public DeploymentDebug(Assembly assembly, String ifInbound, List<CriteriaAtomic> criteriasAtomic, long statsTimeoutMs, long stateTimeoutMs) {
        super("Debug");
        StringBuilder err = new StringBuilder();
        Pcap inPcap = Pcap.openLive(ifInbound, Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, err);
        ConcurrentLinkedQueue<PduAtomic> snifferOut = new ConcurrentLinkedQueue<PduAtomic>();
        ConcurrentLinkedQueue<PduAtomic> statsOut = new ConcurrentLinkedQueue<PduAtomic>();
        ConcurrentLinkedQueue<PduAtomic> stateOut = new ConcurrentLinkedQueue<PduAtomic>();
        ConcurrentLinkedQueue<Pdu> assemblerOut = new ConcurrentLinkedQueue<Pdu>();
        ConcurrentHashMap<CriteriaAtomic, Statistics> statsTableAtomic = new ConcurrentHashMap<CriteriaAtomic, Statistics>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<SocketPair, Connection>();
        ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup = new ConcurrentHashMap<Integer, AssemblerTcp>();
        tcpPortProtocolLookup.put(80, new AssemblerTcpHttp());
        super.components.put("statstable", new ComponentStatisticsTableAtomic(assembly, criteriasAtomic, statsTableAtomic, statsTimeoutMs));
        super.components.put("statetable", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));
        super.components.put("in-sniffer", new ComponentSniffer(assembly, inPcap, snifferOut, true));
        super.components.put("in-statstracker", new ComponentStatisticsTrackerAtomic(assembly, stateOut, statsOut, criteriasAtomic, statsTableAtomic));
        super.components.put("in-statetracker", new ComponentStateTracker(assembly, statsOut, stateOut, stateTable, true));
        super.components.put("in-assembler", new ComponentAssembler(assembly, stateOut, assemblerOut, tcpPortProtocolLookup, null, stateTable));
        super.components.put("in-debugger", new ComponentDebugger(assembly, assemblerOut, null));
    }
}
