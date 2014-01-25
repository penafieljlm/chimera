/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.core;

import java.io.Serializable;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 * @param <T>
 */
public class Diagnostic<T> implements Serializable {

    private final String name;
    private final String desc;
    private final T value;

    public Diagnostic(String name, String desc, T value) {
        this.name = name;
        this.desc = desc;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.desc;
    }

    public T getValue() {
        return this.value;
    }

}
