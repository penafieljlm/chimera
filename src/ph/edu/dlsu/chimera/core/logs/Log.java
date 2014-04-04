package ph.edu.dlsu.chimera.core.logs;

import java.util.Date;

/**
 * An instance of this class constitutes a Log.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Log {

    /**
     * The date and time when the log was created
     */
    public final Date dateTime;

    /**
     * Creates a new Log object.
     *
     * @param dateTime The date and time when the log was created
     */
    public Log(Date dateTime) {
        this.dateTime = dateTime;
    }
}
