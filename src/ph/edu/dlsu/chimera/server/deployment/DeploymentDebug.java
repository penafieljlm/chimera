/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentAssembler;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcpHttp;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentDebug extends Deployment {

    public DeploymentDebug(Assembly assembly,
            String ifInbound,
            Criteria[] criterias,
            long statsTimeoutMs,
            long stateTimeoutMs) {
        super("Debug");
        StringBuilder err = new StringBuilder();
        Pcap inPcap = Pcap.openLive(ifInbound, Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, err);
        ConcurrentLinkedQueue<PduAtomic> snifferOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> statsAtomicOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> stateOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduComposite> assemblerCompositeOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> assemblerAtomicOut = new ConcurrentLinkedQueue<>();

        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup = new ConcurrentHashMap<>();
        tcpPortProtocolLookup.put(80, new AssemblerTcpHttp());

        super.components.put("statstbatomic", new ComponentStatisticsTable(assembly, criterias, statsTableAtomic, statsTimeoutMs));
        super.components.put("statetable", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));

        super.components.put("in-sniffer", new ComponentSniffer(assembly, inPcap, snifferOut, true));
        super.components.put("in-statsatomic", new ComponentStatisticsTracker(assembly, stateOut, statsAtomicOut, criterias, statsTableAtomic));
        super.components.put("in-statetracker", new ComponentStateTracker(assembly, statsAtomicOut, stateOut, stateTable, true));
        super.components.put("in-assembler", new ComponentAssembler(assembly, stateOut, assemblerCompositeOut, assemblerAtomicOut, tcpPortProtocolLookup, null, stateTable));
        //super.components.put("in-debugger", new ComponentDebugger<PduComposite>(assembly, assemblerCompositeOut, null));
    }
}
