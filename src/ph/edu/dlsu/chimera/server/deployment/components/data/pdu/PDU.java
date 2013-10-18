/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.HashMap;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.core.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Pdu {

    public final boolean inbound;
    private final HashMap<Criteria, Statistics> statistics; //type, statistics

    public Pdu(boolean inbound) {
        this.inbound = inbound;
        this.statistics = new HashMap<Criteria, Statistics>();
    }

    public String printDebug() {
        return null;
    }

    public void addStatistics(Criteria criteria, Statistics statistics) {
        for (Criteria crt : this.statistics.keySet()) {
            if (criteria.getClass() == crt.getClass()) {
                return;
            }
        }
        this.statistics.put(criteria, statistics);
    }

    public Statistics getStatisticsByType(Criteria criteria) {
        for (Criteria crt : this.statistics.keySet()) {
            if (criteria.getClass() == crt.getClass()) {
                return this.statistics.get(crt);
            }
        }
        return null;
    }

    public Statistics getStatistics(Criteria criteria) {
        return this.statistics.get(criteria);
    }

    public abstract long size();

    public abstract long timestampInNanos();

}
