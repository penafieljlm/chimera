package ph.edu.dlsu.chimera.core;

import java.util.ArrayList;

/**
 * An instance of this interface constitutes an object that can be queried for
 * diagnostic information.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface IDiagnosable {

    /**
     *
     * @return An ArrayList of diagnostic information
     */
    public ArrayList<Diagnostic> getDiagnostics();
}
