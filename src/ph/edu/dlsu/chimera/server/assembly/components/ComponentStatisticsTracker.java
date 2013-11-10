/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.assembly.components.data.IntermodulePipe;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTracker extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final IntermodulePipe<PduAtomic> outQueue;
    public final Criteria[] criterias;
    public final ConcurrentHashMap<CriteriaInstance, Statistics> statsTable;
    private long processed;

    public ComponentStatisticsTracker(IntermodulePipe<PduAtomic> inQueue,
            IntermodulePipe<PduAtomic> outQueue,
            Criteria[] criterias,
            ConcurrentHashMap<CriteriaInstance, Statistics> statsTable) {
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.criterias = criterias;
        this.statsTable = statsTable;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                if (this.inQueue.isEmpty()) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                while (!this.inQueue.isEmpty()) {
                    synchronized (this.statsTable) {
                        PduAtomic pkt = this.inQueue.poll();
                        synchronized (pkt) {
                            if (pkt.inbound) {
                                //create / update criterias
                                for (Criteria crt : this.criterias) {
                                    CriteriaInstance pktcrt = crt.createInstance(pkt.packet);
                                    if (pktcrt != null) {
                                        if (this.statsTable != null) {
                                            if (!this.statsTable.containsKey(pktcrt)) {
                                                //create criteria
                                                this.statsTable.put(pktcrt, new Statistics(pkt.timestampInNanos));
                                            }
                                            if (this.statsTable.containsKey(pktcrt)) {
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
                                this.processed++;
                                //forward packet
                                if (this.outQueue != null) {
                                    this.outQueue.add(pkt);
                                }
                            } else {
                                throw new Exception("Error: [Statistics Tracker] Encountered outbound packet.");
                            }
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
