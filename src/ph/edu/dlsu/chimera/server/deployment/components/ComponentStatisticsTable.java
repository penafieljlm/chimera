/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTable extends ComponentActive {

    public final List<Criteria> criterias;
    public final ConcurrentHashMap<Criteria, Statistics> statisticsTable;
    public final long statsTimeoutMs;

    public ComponentStatisticsTable(Assembly assembly,
            List<Criteria> criterias,
            ConcurrentHashMap<Criteria, Statistics> statisticsTableAtomic,
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
                for (Criteria crt : this.statisticsTable.keySet()) {
                    if (this.statisticsTable.get(crt).getTimeSinceLastEncounterMs() > this.statsTimeoutMs) {
                        //state timed out
                        this.statisticsTable.remove(crt);
                    }
                }
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        for (Criteria crt : this.criterias) {
            ArrayList<Diagnostic> instances = new ArrayList<Diagnostic>();
            for (Criteria statcrt : this.statisticsTable.keySet()) {
                if (crt.getClass() == statcrt.getClass()) {
                    Statistics stats = this.statisticsTable.get(statcrt);
                    instances.add(new Diagnostic(statcrt.getInstanceString(), statcrt.getInstanceString(), stats.getDiagnostics()));
                }
            }
            diag.add(new Diagnostic(crt.name, crt.description, instances));
        }
        return diag;
    }
}
