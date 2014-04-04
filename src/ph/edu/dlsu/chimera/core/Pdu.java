package ph.edu.dlsu.chimera.core;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.criteria.Criteria;

/**
 * A instance of this class constitutes an object which represents a Protocol
 * Data Unit. It contains information regarding the direction which the Protocol
 * Data Unit was traveling, as well as the timestamp that the Protocol Data Unit
 * had been captured by a Sniffer module.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Pdu {

    /**
     * The direction that the Pdu object was traveling (network ingress or
     * egress)
     */
    public final TrafficDirection direction;
    /**
     * The timestamp that the Pdu had been encountered
     */
    public final long timestampInNanos;
    private final HashMap<Criteria, Statistics> statistics; //type, statistics

    /**
     * Constructs a new Pdu object.
     *
     * @param direction The direction that the Pdu object was traveling (network
     * ingress or egress)
     * @param timestampInNanos The timestamp that the Pdu had been encountered
     */
    public Pdu(TrafficDirection direction, long timestampInNanos) {
        this.direction = direction;
        this.timestampInNanos = timestampInNanos;
        this.statistics = new HashMap<Criteria, Statistics>();
    }

    /**
     * Attaches a piece of statistics information to this Pdu object. The
     * statistics information in question can be used by subsequent modules that
     * will handle this Pdu.
     *
     * @param criteria The criteria which the statistics information is measured
     * for
     * @param statistics The statistics information in question
     */
    public void addStatistics(Criteria criteria, Statistics statistics) {
        this.statistics.put(criteria, statistics);
    }

    /**
     * Returns a piece of statistics information attached to this Pdu object.
     *
     * @param criteria The criteria which the statistics information is measured
     * for
     * @return A piece of statistics information associated with the criteria
     * provided
     */
    public Statistics getStatistics(Criteria criteria) {
        return this.statistics.get(criteria);
    }

    /**
     *
     * @return The size of this Pdu object
     */
    public abstract long size();

}
