/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.messages;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseDiagnose implements Response {

    public final String componentName;
    public final ArrayList<Diagnostic> diagnostics;

    public ResponseDiagnose(String componentName, ArrayList<Diagnostic> diagnostics) {
        this.componentName = componentName;
        this.diagnostics = diagnostics;
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        returned.setReturnedObject(this.diagnostics);
        return new MessageFinished();
    }

}
