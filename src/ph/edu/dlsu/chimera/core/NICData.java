/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.core;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class NICData implements Serializable {

    public final String name;
    public final String netaddress;
    public final String netmask;

    public NICData(String name, String netaddress, String netmask) {
        this.name = name;
        this.netaddress = netaddress;
        this.netmask = netmask;
    }

}
