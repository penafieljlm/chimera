/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface IDiagnosable {

    public ArrayList<Diagnostic> getDiagnostics();
}
