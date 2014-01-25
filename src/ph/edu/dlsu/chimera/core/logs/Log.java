/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.logs;

import java.util.Date;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Log {

    public final Date dateTime;

    public Log(Date dateTime) {
        this.dateTime = dateTime;
    }
}
