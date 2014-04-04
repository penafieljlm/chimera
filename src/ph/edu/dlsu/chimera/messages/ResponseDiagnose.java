package ph.edu.dlsu.chimera.messages;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a Response which contains previously
 * requested Diagnostics information.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseDiagnose implements Response {

    /**
     * Name of the component diagnosed
     */
    public final String componentName;
    /**
     * The Diagnostics information
     */
    public final ArrayList<Diagnostic> diagnostics;

    /**
     * Constructs a ResponseDiagnose object.
     *
     * @param componentName Name of the component diagnosed
     * @param diagnostics The Diagnostics information
     */
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
