/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Diagnostic;
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
    public final ConcurrentHashMap<CriteriaInstance, Statistics> statsTable;
    private long processed;

    public ComponentStatisticsTracker(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            Criteria[] criterias,
            ConcurrentHashMap<CriteriaInstance, Statistics> statsTable) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.criterias = criterias;
        this.statsTable = statsTable;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    synchronized (this.statsTable) {
                        PduAtomic pkt = this.inQueue.poll();
                        if (pkt.inbound) {
                            //create / update criterias
                            for (Criteria crt : this.criterias) {
                                CriteriaInstance pktcrt = crt.createInstance(pkt.packet);
                                if (pktcrt != null) {
                                    if (this.statsTable != null) {
                                        if (!this.statsTable.contains(pktcrt)) {
                                            //create criteria
                                            this.statsTable.put(pktcrt, new Statistics(pkt.timestampInNanos));
                                        }
                                        if (this.statsTable.contains(pktcrt)) {
                                            //update criteria statsTable
                                            this.statsTable.get(pktcrt).commitEncounter(pkt);
                                        }
                                        //associate criteria to packet
                                        pkt.addStatistics(crt, this.statsTable.get(pktcrt));
                                    } else {
                                        throw new Exception("Error: [Statistics Tracker] statisticsTable is null.");
                                    }
                                } else {
                                    pkt.addStatistics(crt, null);
                                }
                            }
                            //forward packet
                            if (this.outQueue != null) {
                                this.processed++;
                                this.outQueue.add(pkt);
                            } else {
                                throw new Exception("Error: [Statistics Tracker] outQueue is null.");
                            }
                        } else {
                            throw new Exception("Error: [Statistics Tracker] Encountered outbound packet.");
                        }
                    }
                }
            } else {
                throw new Exception("Error: [Statistics Tracker] inQueue is null.");
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.statsTable != null) {
            diag.add(new Diagnostic("stats", "State Table Size", this.statsTable.size()));
        } else {
            diag.add(new Diagnostic("stats", "State Table Size", "N/A"));
        }
        if (this.inQueue != null) {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        }
        if (this.outQueue != null) {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        diag.add(new Diagnostic("processed", "Packets Processed", this.processed));
        return diag;
    }
}
