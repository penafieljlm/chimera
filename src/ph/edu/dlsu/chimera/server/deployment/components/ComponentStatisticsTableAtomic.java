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
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.atomic.CriteriaAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatisticsTableAtomic extends ComponentActive {

    public final List<CriteriaAtomic> criteriasAtomic;
    public final ConcurrentHashMap<CriteriaAtomic, Statistics> statisticsTableAtomic;
    public final long statsTimeoutMs;

    public ComponentStatisticsTableAtomic(Assembly assembly,
            List<CriteriaAtomic> criteriasAtomic,
            ConcurrentHashMap<CriteriaAtomic, Statistics> statisticsTableAtomic,
            long statsTimeoutMs) {
        super(assembly);
        this.setPriority(Thread.MIN_PRIORITY);
        this.criteriasAtomic = criteriasAtomic;
        this.statisticsTableAtomic = statisticsTableAtomic;
        this.statsTimeoutMs = statsTimeoutMs;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.statisticsTableAtomic != null) {
                for (CriteriaAtomic crt : this.statisticsTableAtomic.keySet()) {
                    if (this.statisticsTableAtomic.get(crt).getTimeSinceLastEncounter() > this.statsTimeoutMs) {
                        //state timed out
                        this.statisticsTableAtomic.remove(crt);
                    }
                }
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        for (CriteriaAtomic crt : this.criteriasAtomic) {
            ArrayList<Diagnostic> instances = new ArrayList<Diagnostic>();
            for (CriteriaAtomic statcrt : this.statisticsTableAtomic.keySet()) {
                if (crt.getClass() == statcrt.getClass()) {
                    Statistics stats = this.statisticsTableAtomic.get(statcrt);
                    instances.add(new Diagnostic(statcrt.getInstanceString(), statcrt.getInstanceString(), stats.getDiagnostics()));
                }
            }
            diag.add(new Diagnostic(crt.name, crt.description, instances));
        }
        return diag;
    }
}
