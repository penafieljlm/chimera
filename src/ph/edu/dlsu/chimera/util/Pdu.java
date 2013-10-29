/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.util;

import java.util.HashMap;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.core.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Pdu {

    public final boolean inbound;
    public final long timestampInNanos;
    private final HashMap<Criteria, Statistics> statistics; //type, statistics

    public Pdu(boolean inbound, long timestampInNanos) {
        this.inbound = inbound;
        this.timestampInNanos = timestampInNanos;
        this.statistics = new HashMap<>();
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

    public Statistics getStatistics(Criteria criteria) {
        return this.statistics.get(criteria);
    }

    public abstract long size();

}
