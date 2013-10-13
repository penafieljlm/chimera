/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats.composite;

import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class CriteriaComposite extends Criteria {

    public CriteriaComposite(String name, String description) {
        super(name, description);
    }

    /**
     * @param pdu - pdu to create criteria instance from.
     * @return an instance of a criteria type from the packet; null if packet is not compatible with the criteria type.
     */
    public abstract CriteriaComposite createInstance(PduComposite pdu);
}
