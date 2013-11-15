/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class TimeUtils {

    public static long nowNs() {
        return TimeUtils.msToNs(System.currentTimeMillis());
    }

    public static long nowMs() {
        return System.currentTimeMillis();
    }

    public static long msToNs(long ms) {
        return ms * 1000000;
    }

    public static long nsToMs(long ns) {
        return ns / 1000000;
    }
}
