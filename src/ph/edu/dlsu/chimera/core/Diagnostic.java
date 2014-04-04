package ph.edu.dlsu.chimera.core;

import java.io.Serializable;

/**
 * An instance of this class constitutes a piece of diagnostic information,
 * complete with a name, a description, and a value.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 * @param <T> The type of the diagnostic information value
 */
public class Diagnostic<T> implements Serializable {

    private final String name;
    private final String desc;
    private final T value;

    /**
     * Constructs a new Diagnostic object.
     *
     * @param name Name of the diagnostic information
     * @param desc Description of the diagnostic information
     * @param value Value of the diagnostic information
     */
    public Diagnostic(String name, String desc, T value) {
        this.name = name;
        this.desc = desc;
        this.value = value;
    }

    /**
     *
     * @return The name of the diagnostic information
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return The description of the diagnostic information
     */
    public String getDescription() {
        return this.desc;
    }

    /**
     *
     * @return The value of the diagnostic information
     */
    public T getValue() {
        return this.value;
    }

}
