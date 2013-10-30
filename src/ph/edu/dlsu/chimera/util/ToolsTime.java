/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.Date;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ToolsTime {

    public static long nowNs() {
        Date now = new Date();
        return ToolsTime.msToNs(now.getTime());
    }

    public static long nowMs() {
        Date now = new Date();
        return now.getTime();
    }

    public static long msToNs(long ms) {
        return ms * 1000000;
    }

    public static long nsToMs(long ns) {
        return ns / 1000000;
    }
}
