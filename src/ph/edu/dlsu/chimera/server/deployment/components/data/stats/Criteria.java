/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Criteria {

    public final String name;
    public final String description;

    public Criteria(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @param pkt - packet to create criteria instance from.
     * @return an instance of a criteria type from the packet; null if packet is not compatible with the criteria type.
     */
    public abstract Criteria createInstance(PduAtomic pkt);

    /**
     * @return a String object describing this Criteria instance.
     */
    public abstract String getInstanceString();

}
