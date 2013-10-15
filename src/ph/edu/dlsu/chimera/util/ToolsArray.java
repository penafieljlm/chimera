/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ToolsArray {

    public static byte[] concat(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    public static byte[] concat(byte[]... arg) {
        byte[] result = new byte[0];
        for (byte[] a : arg) {
            result = ToolsArray.concat(result, a);
        }
        return result;
    }

    public static <T extends Object> T[] concat(T[] left, T[] right) {
        Object[] result = new Object[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return (T[]) result;
    }

    public static <T extends Object> T[] concat(T[]... arg) {
        Object[] result = new Object[0];
        for (Object[] a : arg) {
            result = ToolsArray.concat(result, a);
        }
        return (T[]) result;
    }
}
