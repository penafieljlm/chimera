/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsTime {

    public static long nowNs() {
        return UtilsTime.msToNs(System.currentTimeMillis());
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
