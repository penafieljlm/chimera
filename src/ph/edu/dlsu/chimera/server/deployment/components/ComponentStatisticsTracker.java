/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTracker extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final List<Criteria> criterias;
    public final ConcurrentHashMap<Criteria, Statistics> statisticsTable;

    public ComponentStatisticsTracker(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            List<Criteria> criterias,
            ConcurrentHashMap<Criteria, Statistics> statisticsTable) {
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
                        //processing here
                        Criteria[] pktcriterias = new Criteria[this.criterias.size()];
                        //get criterias for this packet
                        for (int i = 0; i < this.criterias.size(); i++) {
                            pktcriterias[i] = this.criterias.get(i).createInstance(pkt);
                        }
                        //create / update criterias
                        for (Criteria crt : pktcriterias) {
                            if (crt != null) {
                                if (!this.statisticsTable.contains(crt)) {
                                    //create criteria
                                    this.statisticsTable.put(crt, new Statistics(pkt.timestampInNanos()));
                                }
                                if (this.statisticsTable.contains(crt)) {
                                    //update criteria statisticsTable and associate criteria to packet
                                    this.statisticsTable.get(crt).commitEncounter(pkt);
                                    pkt.addStatistics(crt, this.statisticsTable.get(crt));
                                }
                            }
                        }
                        //forward packet
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                    }
                }
            }
        }
    }
}
