/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats.atomic;

import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class CriteriaAtomic extends Criteria {

    public CriteriaAtomic(String name, String description) {
        super(name, description);
    }

    /**
     * @param pkt - packet to create criteria instance from.
     * @return an instance of a criteria type from the packet; null if packet is not compatible with the criteria type.
     */
    public abstract CriteriaAtomic createInstance(PduAtomic pkt);
}
