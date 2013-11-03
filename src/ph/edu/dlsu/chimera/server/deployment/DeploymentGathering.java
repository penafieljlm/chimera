/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import com.gremwell.jnetbridge.PcapPort;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Statistics;
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

    public DeploymentGathering(PcapPort ifExternalPcapPort,
            PcapPort ifInternalPcapPort,
            Criteria[] criterias,
            File trainingDumpFile,
            boolean gatherAttacks,
            long statsTimeoutMs,
            long stateTimeoutMs) throws Exception {
        //deployment name
        super("Data Gathering");

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
        super.addComponent("ex.gather.sniff", new ComponentSniffer(ifExternalPcapPort, exGatherSniffOut, true));
        super.addComponent("ex.gather.stats", new ComponentStatisticsTracker(exGatherSniffOut, exGatherStatsOut, criterias, statsTableAtomic));
        super.addComponent("ex.gather.states", new ComponentStateTracker(exGatherStatsOut, exGatherStateOut, stateTable));
        super.addComponent("ex.gather.preprc", new ComponentInstancePreprocessor(exGatherStateOut, exGatherPrePrcOut, criterias, gatherAttacks));
        super.addComponent("ex.gather.dumper", new ComponentInstanceDumper(exGatherPrePrcOut, criterias, trainingDumpFile));

        //outbound path
        super.addComponent("in.gather.sniff", new ComponentSniffer(ifInternalPcapPort, inGatherSniffOut, false));
        super.addComponent("in.gather.states", new ComponentStateTracker(inGatherSniffOut, null, stateTable));
    }
}
