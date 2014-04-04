package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;

/**
 * An instance of this class constitutes a Component which enforces the
 * expiration of tracked criteria instances.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsDaemon extends ComponentActive {

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
     * The amount of time a criteria instance is allowed to be inactive
     */
    public final long statsTimeoutMs;

    /**
     * Constructs a new ComponentStatisticsDaemon object.
     *
     * @param criterias The set of criteria which statistics are being measured
     * for
     * @param statsTable A Hashmap which assigns criteria statistic monitors to
     * CriteriaInstance objects
     * @param statsTimeoutMs The amount of time a criteria instance is allowed
     * to be inactive
     */
    public ComponentStatisticsDaemon(Criteria[] criterias,
            ConcurrentHashMap<CriteriaInstance, Statistics> statsTable,
            long statsTimeoutMs) {
        this.setPriority(Thread.MIN_PRIORITY);
        this.criterias = criterias;
        this.statsTable = statsTable;
        this.statsTimeoutMs = statsTimeoutMs;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.statsTable != null) {
                synchronized (this.statsTable) {
                    for (CriteriaInstance crt : this.statsTable.keySet()) {
                        if (this.statsTable.get(crt).getTimeSinceLastEncounterMs() > this.statsTimeoutMs) {
                            //stats timed out
                            this.statsTable.remove(crt);
                        }
                    }
                }
            } else {
                throw new Exception("Error: [Statistics Daemon] statisticsTable is null.");
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.statsTable != null) {
            synchronized (this.statsTable) {
                ArrayList<Diagnostic> _criterias = new ArrayList<Diagnostic>();
                for (Criteria crt : this.criterias) {
                    ArrayList<Diagnostic> instances = new ArrayList<Diagnostic>();
                    for (CriteriaInstance crtinst : this.statsTable.keySet()) {
                        if (crtinst.criteria.equals(crt)) {
                            instances.add(new Diagnostic(crtinst.getStringId(), crtinst.getStringId(), this.statsTable.get(crtinst).getDiagnostics()));
                        }
                    }
                    _criterias.add(new Diagnostic(crt.expression, crt.expression, instances));
                }
                diag.add(new Diagnostic("stats", "Statistics Instances", _criterias));
            }
        }
        return diag;
    }
}
