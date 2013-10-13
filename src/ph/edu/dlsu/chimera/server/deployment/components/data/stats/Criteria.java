/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

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
     * @return a String object describing this CriteriaAtomic instance.
     */
    public abstract String getInstanceString();
}
