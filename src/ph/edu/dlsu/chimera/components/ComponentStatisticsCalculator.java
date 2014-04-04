package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.PduAtomic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;

/**
 * An instance of this class constitutes a Component which invokes functions
 * that create, update, and delete statistic monitors for a user-specified set
 * of criteria.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsCalculator extends ComponentActiveProcessorPdu<PduAtomic, PduAtomic> {

    /**
     * The set of criteria which statistics are being measured for
     */
    public final Criteria[] criterias;
    /**
     * A Hashmap which assigns criteria statistic monitors to CriteriaInstance
     * objects
     */
    public final ConcurrentHashMap<CriteriaInstance, Statistics> statsTable;

    /**
     * Constructs a new ComponentStatisticsCalculator object.
     *
     * @param inQueue The input queue
     * @param outQueue The output queue
     * @param criterias The set of criteria for which statistics are being
     * measured for
     * @param statsTable A Hashmap which assigns criteria statistic monitors to
     * CriteriaInstance objects
     */
    public ComponentStatisticsCalculator(IntermodulePipe<PduAtomic> inQueue,
            IntermodulePipe<PduAtomic> outQueue,
            Criteria[] criterias,
            ConcurrentHashMap<CriteriaInstance, Statistics> statsTable) {
        super(inQueue, outQueue);
        this.setPriority(Thread.NORM_PRIORITY);
        this.criterias = criterias;
        this.statsTable = statsTable;
    }

    @Override
    protected PduAtomic process(PduAtomic input) throws Exception {
        synchronized (this.statsTable) {
            if (input.direction == TrafficDirection.Ingress) {
                //create / update criterias
                for (Criteria crt : this.criterias) {
                    CriteriaInstance pktcrt = crt.createInstance(input.packet);
                    if (pktcrt != null) {
                        if (this.statsTable != null) {
                            if (!this.statsTable.containsKey(pktcrt)) {
                                //create criteria
                                this.statsTable.put(pktcrt, new Statistics(input.timestampInNanos));
                            }
                            if (this.statsTable.containsKey(pktcrt)) {
                                //update criteria statsTable
                                this.statsTable.get(pktcrt).commitEncounter(input.timestampInNanos, input.size());
                            }
                            //associate criteria to packet
                            input.addStatistics(crt, this.statsTable.get(pktcrt));
                        } else {
                            throw new Exception("Error: [Statistics Calculator] statisticsTable is null.");
                        }
                    } else {
                        input.addStatistics(crt, null);
                    }
                }
                //forward packet
                return input;
            } else {
                throw new Exception("Error: [Statistics Calculator] Encountered egress packet.");
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
        return diag;
    }
}
