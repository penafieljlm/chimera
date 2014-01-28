/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.logs;

import java.util.Date;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.TcpSocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class LogAttackConnection extends LogAttack {

    public Connection connection;

    public LogAttackConnection(Date dateTime, Connection connection) {
        super(dateTime);
        this.connection = connection;
    }
}
