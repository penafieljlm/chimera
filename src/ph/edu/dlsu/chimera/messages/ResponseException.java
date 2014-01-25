package ph.edu.dlsu.chimera.messages;

import java.io.PrintStream;
import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a message reporting an exceptions.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseException implements Response {

    public final Exception exceptions;

    public ResponseException(Exception exception) {
        this.exceptions = exception;
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        returned.setReturnedObject(this.exceptions);
        return new MessageFinished();
    }

}
