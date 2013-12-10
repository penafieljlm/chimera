/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTable extends ComponentActive {

    public final Criteria[] criterias;
    public final ConcurrentHashMap<CriteriaInstance, Statistics> statsTable;
    public final long statsTimeoutMs;

    public ComponentStatisticsTable(Criteria[] criterias,
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
                throw new Exception("Error: [Statistics Table] statisticsTable is null.");
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.statsTable != null) {
            synchronized (this.statsTable) {
                ArrayList<Diagnostic> _criterias = new ArrayList<>();
                for (Criteria crt : this.criterias) {
                    ArrayList<Diagnostic> instances = new ArrayList<>();
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
