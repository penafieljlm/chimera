/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.rules.RuleInfo;
import ph.edu.dlsu.chimera.rules.RulesManager;

/**
 *
 * @author Nikkol
 */
public class ComponentRulesDaemon extends ComponentActive {

    public final RulesManager rulesManager;
    public final long rulesTimeoutMs;

    public ComponentRulesDaemon(RulesManager rulesManager, long rulesTimeoutMs) {
        this.rulesManager = rulesManager;
        this.rulesTimeoutMs = rulesTimeoutMs;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.rulesManager != null) {
                synchronized (this.rulesManager) {
                    //integrity check
                    if (this.rulesManager != null && this.rulesManager.isTampered()) {
                        throw new Exception("Error: [Rules Daemon] The CHIMERA iptables chain has been tampered with.");
                    }
                    //check timouts and delete
                    boolean ok;
                    do {
                        ok = true;
                        ArrayList<RuleInfo> r = this.rulesManager.getRulesMap();
                        for (int i = 0; i < r.size(); i++) {
                            if (System.currentTimeMillis() - r.get(i).timeCreatedMs > this.rulesTimeoutMs) {
                                //timeout
                                this.rulesManager.remove(i);
                                ok = false;
                                break;
                            }
                        }
                    } while (!ok);
                    if (this.rulesManager.hasUncommitedChanges()) {
                        this.rulesManager.commit();
                    }
                }
            } else {
                throw new Exception("Error: [Rules Daemon] rulesManager is null.");
            }
        }
    }

    @Override
    public ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        synchronized (this.rulesManager) {
            diag.add(new Diagnostic("rules", "Rule Count", this.rulesManager.getRulesMap().size()));
        }
        return diag;
    }
}
