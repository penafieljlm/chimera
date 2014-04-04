package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes an Response which acknowledges that the
 * system had stopped its deployment process.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseQuit implements Response {

    @Override
    public Command handleResponse(ReturnParameter returned) {
        return new MessageFinished();
    }

}
