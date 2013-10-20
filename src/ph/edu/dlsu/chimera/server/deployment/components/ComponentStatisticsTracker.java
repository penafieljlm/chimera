/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTracker extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final Criteria[] criterias;
    public final ConcurrentHashMap<CriteriaInstance, Statistics> statisticsTable;

    public ComponentStatisticsTracker(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            Criteria[] criterias,
            ConcurrentHashMap<CriteriaInstance, Statistics> statisticsTable) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.criterias = criterias;
        this.statisticsTable = statisticsTable;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        //create / update criterias
                        for (Criteria crt : this.criterias) {
                            CriteriaInstance pktcrt = crt.createInstance(pkt.packet);
                            if (pktcrt != null) {
                                if (this.statisticsTable != null) {
                                    if (!this.statisticsTable.contains(pktcrt)) {
                                        //create criteria
                                        this.statisticsTable.put(pktcrt, new Statistics(pkt.timestampInNanos()));
                                    }
                                    if (this.statisticsTable.contains(pktcrt)) {
                                        //update criteria statisticsTable
                                        this.statisticsTable.get(pktcrt).commitEncounter(pkt);
                                    }
                                    //associate criteria to packet
                                    pkt.addStatistics(crt, this.statisticsTable.get(pktcrt));
                                } else {
                                    throw new Exception("Error: [Statistics Tracker] statisticsTable is null.");
                                }
                            } else {
                                pkt.addStatistics(crt, null);
                            }
                        }
                        //forward packet
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        } else {
                            throw new Exception("Error: [Statistics Tracker] outQueue is null.");
                        }
                    } else {
                        throw new Exception("Error: [Statistics Tracker] Encountered outbound packet.");
                    }
                }
            } else {
                throw new Exception("Error: [Statistics Tracker] inQueue is null.");
            }
        }
    }
}
