package ph.edu.dlsu.chimera.core.logs;

import java.util.Date;

/**
 * An instance of this class constitutes a type of Log which deals with an
 * attack.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class LogAttack extends Log {

    /**
     * Constructs a new LogAttack object.
     *
     * @param dateTime The date and time when the log was created
     */
    public LogAttack(Date dateTime) {
        super(dateTime);
    }
}
