/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data;

import net.sourceforge.jpcap.util.Timeval;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConnectionDataTCP extends ConnectionData {

    public ConnectionDataTCP(Connection connection, Timeval timeCreated) {
        super(connection, timeCreated);
    }

}
