/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ToolsArray {

    public static byte[] reverse(byte[] arg) {
        byte[] ok = new byte[arg.length];
        for (int i = 0; i < arg.length; i++) {
            ok[i] = arg[arg.length - (i + 1)];
        }
        return ok;
    }

    public static byte[] concat(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    public static byte[] concat(byte[] left, byte[]... right) {
        byte[] result = left;
        for (byte[] a : right) {
            result = ToolsArray.concat(result, a);
        }
        return result;
    }

    public static <T> T[] concat(T[] left, T[] right) {
        T[] result = (T[]) Array.newInstance(left.getClass().getComponentType(), left.length + right.length);
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    public static <T> T[] concat(T[] left, T[]... right) {
        T[] result = left;
        for (T[] a : right) {
            result = ToolsArray.concat(result, a);
        }
        return result;
    }

    public static <T> T[] append(T[] a, T e) {
        T[] o = (T[]) Array.newInstance(a.getClass().getComponentType(), 1);
        o[0] = e;
        return ToolsArray.concat(a, o);
    }

    public static <T> T[] append(T[] a, T... e) {
        T[] ta = a;
        for (T t : e) {
            ta = ToolsArray.append(ta, t);
        }
        return ta;
    }
}
