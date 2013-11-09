/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly;

import com.gremwell.jnetbridge.PcapPort;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentInstanceDumper;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentInstancePreprocessor;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.server.assembly.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.core.reflection.PacketFilter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class AssemblyGathering extends Assembly {

    public AssemblyGathering(int controlPort,
            PcapPort ifExternalPcapPort,
            PcapPort ifInternalPcapPort,
            Criteria[] criterias,
            File trainingDumpFile,
            PacketFilter excludeFilter,
            PacketFilter filter,
            boolean tagFilteredAsAttacks,
            long statsTimeoutMs,
            long stateTimeoutMs) throws Exception {
        super("Data Gathering", controlPort);

        //inbound queues
        IntermodulePipe<PduAtomic> exGatherSniffOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exGatherStatsOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exGatherPrePrcOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exGatherStateOut = new IntermodulePipe<>();

        //outbound queues
        IntermodulePipe<PduAtomic> inGatherSniffOut = new IntermodulePipe<>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        //daemons
        super.addComponent("stats", new ComponentStatisticsTable(criterias, statsTableAtomic, statsTimeoutMs));
        super.addComponent("states", new ComponentStateTable(stateTable, stateTimeoutMs));

        //inbound path
        super.addComponent("ex.gather.sniff", new ComponentSniffer(ifExternalPcapPort, exGatherSniffOut, excludeFilter, false, true));
        super.addComponent("ex.gather.stats", new ComponentStatisticsTracker(exGatherSniffOut, exGatherStatsOut, criterias, statsTableAtomic));
        super.addComponent("ex.gather.states", new ComponentStateTracker(exGatherStatsOut, exGatherStateOut, stateTable));
        super.addComponent("ex.gather.preprc", new ComponentInstancePreprocessor(exGatherStateOut, exGatherPrePrcOut, criterias, filter, tagFilteredAsAttacks));
        super.addComponent("ex.gather.dumper", new ComponentInstanceDumper(exGatherPrePrcOut, criterias, trainingDumpFile));

        //outbound path
        super.addComponent("in.gather.sniff", new ComponentSniffer(ifInternalPcapPort, inGatherSniffOut, excludeFilter, false, false));
        super.addComponent("in.gather.states", new ComponentStateTracker(inGatherSniffOut, null, stateTable));
    }
}
