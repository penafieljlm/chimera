/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.iptables;

import de.tbsol.iptablesjava.rules.IpRule;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class IpTablesChain {

    public final String name;
    public final IpRule[] rules;

    public IpTablesChain(String name, IpRule[] rules) {
        this.name = name;
        this.rules = rules;
    }
}
