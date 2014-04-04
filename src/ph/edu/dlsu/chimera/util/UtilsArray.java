package ph.edu.dlsu.chimera.util;

import java.lang.reflect.Array;

/**
 * This utility class provide functions dealing with arrays.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsArray {

    /**
     * Reverses a byte array.
     *
     * @param arg The byte array
     * @return The reversed byte array
     */
    public static byte[] reverse(byte[] arg) {
        byte[] ok = new byte[arg.length];
        for (int i = 0; i < arg.length; i++) {
            ok[i] = arg[arg.length - (i + 1)];
        }
        return ok;
    }

    /**
     * Concatenates two byte arrays.
     *
     * @param left The left byte array
     * @param right The right byte array
     * @return The concatenation of the byte arrays
     */
    public static byte[] concat(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    /**
     * Concatenates multiple byte arrays.
     *
     * @param left The left byte array
     * @param right The right byte arrays
     * @return The concatenation of the byte arrays
     */
    public static byte[] concat(byte[] left, byte[]  
        ... right) {
        byte[] result = left;
        for (byte[] a : right) {
            result = UtilsArray.concat(result, a);
        }
        return result;
    }

    /**
     * Concatenates two object arrays.
     *
     * @param <T> The type of objects inside the arrays
     * @param left The left array
     * @param right The right array
     * @return The concatenation of the arrays
     */
    public static <T> T[] concat(T[] left, T[] right) {
        T[] result = (T[]) Array.newInstance(left.getClass().getComponentType(), left.length + right.length);
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    /**
     * Concatenates multiple byte arrays.
     *
     * @param <T> The type of objects inside the arrays
     * @param left The left array
     * @param right The right arrays
     * @return The concatenation of the arrays
     */
    public static <T> T[] concat(T[] left, T[]  
        ... right) {
        T[] result = left;
        for (T[] a : right) {
            result = UtilsArray.concat(result, a);
        }
        return result;
    }

    /**
     * Appends the specified element onto the specified array.
     *
     * @param <T> The type of objects being dealt with
     * @param a The array
     * @param e The element to be appended
     * @return The array with the appended element
     */
    public static <T> T[] append(T[] a, T e) {
        T[] o = (T[]) Array.newInstance(a.getClass().getComponentType(), 1);
        o[0] = e;
        return UtilsArray.concat(a, o);
    }

    /**
     * Appends the specified elements onto the specified array.
     *
     * @param <T> The type of objects being dealt with
     * @param a The array
     * @param e The elements to be appended
     * @return The array with the appended elements
     */
    public static <T> T[] append(T[] a, T... e) {
        T[] ta = a;
        for (T t : e) {
            ta = UtilsArray.append(ta, t);
        }
        return ta;
    }

    /**
     * Transforms an array of objects into an array of Strings.
     *
     * @param a The array of objects
     * @return The array of String
     */
    public static String[] toCsv(Object[] a) {
        String[] _a = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            _a[i] = "" + a[i];
        }
        return _a;
    }
}
