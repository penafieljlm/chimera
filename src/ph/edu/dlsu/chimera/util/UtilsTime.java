package ph.edu.dlsu.chimera.util;

/**
 * This utility class provide functions dealing with time.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsTime {

    /**
     *
     * @return Current timestamp in nanoseconds
     */
    public static long nowNs() {
        return UtilsTime.msToNs(System.currentTimeMillis());
    }

    /**
     *
     * @return Current timestamp in milliseconds
     */
    public static long nowMs() {
        return System.currentTimeMillis();
    }

    /**
     * Converts milliseconds to nanoseconds
     *
     * @param ms Milliseconds
     * @return Nanoseconds
     */
    public static long msToNs(long ms) {
        return ms * 1000000;
    }

    /**
     * Converts nanoseconds to milliseconds
     *
     * @param ns Nanoseconds
     * @return Milliseconds
     */
    public static long nsToMs(long ns) {
        return ns / 1000000;
    }
}
