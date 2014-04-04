package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a Response reporting an Exception.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseException implements Response {

    /**
     * The reported Exception
     */
    public final Exception exception;

    /**
     * Constructs a new ResponseException object.
     *
     * @param exception The reported Exception
     */
    public ResponseException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        returned.setReturnedObject(this.exception);
        return new MessageFinished();
    }

}
