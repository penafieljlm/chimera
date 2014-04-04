package ph.edu.dlsu.chimera.core.logs;

import java.util.Date;
import ph.edu.dlsu.chimera.core.Connection;

/**
 * An instance of this class constitutes a type of Log which deals with attacks
 * associated with a TCP Stream.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class LogAttackConnection extends LogAttack {

    /**
     * The TCP Stream monitor associated with the detected attack
     */
    public Connection connection;

    /**
     *
     * @param dateTime The date and time when the log was created
     * @param connection The TCP Stream monitor associated with the detected
     * attack
     */
    public LogAttackConnection(Date dateTime, Connection connection) {
        super(dateTime);
        this.connection = connection;
    }
}
