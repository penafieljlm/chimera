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

    public DeploymentGathering(Assembly assembly,
            int ifExternal,
            int ifInternal,
            String dumpFileName,
            boolean gatherAttacks,
            long statsTimeoutMs,
            long stateTimeoutMs) throws Exception {
        //deployment name
        super("Data Gathering");

        //get external pcap interface
        PcapIf externalPcapIf = null;
        try {
            externalPcapIf = assembly.getInterfaces().get(ifExternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifExternal + "' does not exist.");
        }

        //get internal pcap interface
        PcapIf internalPcapIf = null;
        try {
            internalPcapIf = assembly.getInterfaces().get(ifInternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifInternal + "' does not exist.");
        }

        //generic resources
        File trainingDumpFile = new File(dumpFileName);
        StringBuilder inErr = new StringBuilder();
        StringBuilder outErr = new StringBuilder();

        //packet capture objects
        Pcap externalPcap = Pcap.openLive(externalPcapIf.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, inErr);
        Pcap internalPcap = Pcap.openLive(internalPcapIf.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, outErr);

        //inbound queues
        ConcurrentLinkedQueue<PduAtomic> inSniffOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> inForkOutInject = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> inForkOutGather = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> inStatsOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> inStateOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> inPrePrcOut = new ConcurrentLinkedQueue<>();

        //outbound queues
        ConcurrentLinkedQueue<PduAtomic> outSniffOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> outForkOutInject = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> outForkOutGather = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PduAtomic> outStateOut = new ConcurrentLinkedQueue<>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        //daemons
        super.components.put("stats", new ComponentStatisticsTable(assembly, assembly.getCriterias(), statsTableAtomic, statsTimeoutMs));
        super.components.put("states", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));

        //inbound path
        //path 1 = sniffer  ->  injector
        //path 2 = sniffer  ->  stats   ->  states  ->  preprc  ->  dumper
        super.components.put("in-sniff", new ComponentSniffer(assembly, externalPcap, inSniffOut, true));
        super.components.put("in-fork", new ComponentFork(assembly, inSniffOut, inForkOutInject, inForkOutGather));
        super.components.put("in-stats", new ComponentStatisticsTracker(assembly, inForkOutGather, inStatsOut, assembly.getCriterias(), statsTableAtomic));
        super.components.put("in-states", new ComponentStateTracker(assembly, inStatsOut, inStateOut, stateTable));
        super.components.put("in-preprc", new ComponentInstancePreprocessor(assembly, inStateOut, inPrePrcOut, assembly.getCriterias(), gatherAttacks));
        super.components.put("in-dumper", new ComponentInstanceDumper(assembly, inPrePrcOut, assembly.getCriterias(), trainingDumpFile));
        super.components.put("in-inject", new ComponentInjector(assembly, inForkOutInject, internalPcap));

        //outbound path
        super.components.put("out-sniff", new ComponentSniffer(assembly, internalPcap, outSniffOut, false));
        super.components.put("out-fork", new ComponentFork(assembly, outSniffOut, outForkOutInject, outForkOutGather));
        super.components.put("out-states", new ComponentStateTracker(assembly, outForkOutGather, outStateOut, stateTable));
        super.components.put("out-inject", new ComponentInjector(assembly, outForkOutInject, externalPcap));
    }
}
