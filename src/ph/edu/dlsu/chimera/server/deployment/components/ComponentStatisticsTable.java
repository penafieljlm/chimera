/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTable extends ComponentActive {

    public final Criteria[] criterias;
    public final ConcurrentHashMap<CriteriaInstance, Statistics> statisticsTable;
    public final long statsTimeoutMs;

    public ComponentStatisticsTable(Assembly assembly,
            Criteria[] criterias,
            ConcurrentHashMap<CriteriaInstance, Statistics> statisticsTableAtomic,
            long statsTimeoutMs) {
        super(assembly);
        this.setPriority(Thread.MIN_PRIORITY);
        this.criterias = criterias;
        this.statisticsTable = statisticsTableAtomic;
        this.statsTimeoutMs = statsTimeoutMs;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.statisticsTable != null) {
                for (CriteriaInstance crt : this.statisticsTable.keySet()) {
                    if (this.statisticsTable.get(crt).getTimeSinceLastEncounterMs() > this.statsTimeoutMs) {
                        //stats timed out
                        this.statisticsTable.remove(crt);
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
        for (Criteria crt : this.criterias) {
            ArrayList<Diagnostic> instances = new ArrayList<>();
            int instCt = 0;
            for (CriteriaInstance crtinst : this.statisticsTable.keySet()) {
                if (crtinst.criteria.equals(crt)) {
                    instCt++;
                }
            }
            instances.add(new Diagnostic("count", "Instance Count", instCt));
            diag.add(new Diagnostic(crt.expression, crt.expression, instances));
        }
        return diag;
    }
}
