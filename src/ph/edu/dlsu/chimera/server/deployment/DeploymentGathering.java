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
import ph.edu.dlsu.chimera.server.deployment.components.data.IntermodulePipe;
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
        PcapIf exPcapIf = null;
        try {
            exPcapIf = assembly.getInterfaces().get(ifExternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifExternal + "' does not exist.");
        }

        //get internal pcap interface
        PcapIf inPcapIf = null;
        try {
            inPcapIf = assembly.getInterfaces().get(ifInternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifInternal + "' does not exist.");
        }

        //generic resources
        File trainingDumpFile = new File(dumpFileName);
        StringBuilder inErr = new StringBuilder();
        StringBuilder outErr = new StringBuilder();

        //packet capture objects
        Pcap externalPcap = Pcap.openLive(exPcapIf.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Defaults.DEFAULT_TIMEOUT_PCAP_MS, inErr);
        Pcap internalPcap = Pcap.openLive(inPcapIf.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Defaults.DEFAULT_TIMEOUT_PCAP_MS, outErr);

        //inbound queues
        IntermodulePipe<PduAtomic> exSniffOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exForkOutInject = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exForkOutGather = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exStatsOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exStateOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exPrePrcOut = new IntermodulePipe<>();

        //outbound queues
        IntermodulePipe<PduAtomic> inSniffOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> inForkOutInject = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> inForkOutGather = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> inStateOut = new IntermodulePipe<>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        //daemons
        super.components.put("stats", new ComponentStatisticsTable(assembly, assembly.getCriterias(), statsTableAtomic, statsTimeoutMs));
        super.components.put("states", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));

        //inbound path
        //path 1 = sniffer  ->  injector
        //path 2 = sniffer  ->  stats   ->  states  ->  preprc  ->  dumper
        super.components.put("ex-sniff", new ComponentSniffer(assembly, externalPcap, exSniffOut, true));
        super.components.put("ex-fork", new ComponentFork(assembly, exSniffOut, exForkOutInject, exForkOutGather));
        super.components.put("ex-stats", new ComponentStatisticsTracker(assembly, exForkOutGather, exStatsOut, assembly.getCriterias(), statsTableAtomic));
        super.components.put("ex-states", new ComponentStateTracker(assembly, exStatsOut, exStateOut, stateTable));
        super.components.put("ex-preprc", new ComponentInstancePreprocessor(assembly, exStateOut, exPrePrcOut, assembly.getCriterias(), gatherAttacks));
        super.components.put("ex-dumper", new ComponentInstanceDumper(assembly, exPrePrcOut, assembly.getCriterias(), trainingDumpFile));
        super.components.put("ex-inject", new ComponentInjector(assembly, exForkOutInject, internalPcap));

        //outbound path
        super.components.put("in-sniff", new ComponentSniffer(assembly, internalPcap, inSniffOut, false));
        super.components.put("in-fork", new ComponentFork(assembly, inSniffOut, inForkOutInject, inForkOutGather));
        super.components.put("in-states", new ComponentStateTracker(assembly, inForkOutGather, null, stateTable));
        super.components.put("in-inject", new ComponentInjector(assembly, inForkOutInject, externalPcap));
    }
}
