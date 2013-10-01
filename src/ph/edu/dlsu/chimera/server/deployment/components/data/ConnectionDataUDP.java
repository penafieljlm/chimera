/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ConnectionDataUDP extends ConnectionData {

    public ConnectionDataUDP(Connection connection, long timeCreated, boolean inbound) {
        super(connection, timeCreated, inbound);
    }
    
}
