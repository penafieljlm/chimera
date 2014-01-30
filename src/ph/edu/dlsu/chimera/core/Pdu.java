/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.criteria.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Pdu {

    public final TrafficDirection direction;
    public final long timestampInNanos;
    private final HashMap<Criteria, Statistics> statistics; //type, statistics

    public Pdu(TrafficDirection direction, long timestampInNanos) {
        this.direction = direction;
        this.timestampInNanos = timestampInNanos;
        this.statistics = new HashMap<Criteria, Statistics>();
    }

    public void addStatistics(Criteria criteria, Statistics statistics) {
        this.statistics.put(criteria, statistics);
    }

    public Statistics getStatistics(Criteria criteria) {
        return this.statistics.get(criteria);
    }

    public abstract long size();

}