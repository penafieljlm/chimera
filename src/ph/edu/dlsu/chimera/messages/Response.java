package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a response message used by the system
 * to communicate with connected clients.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface Response extends Message {

    /**
     * Client side function which executes the contents of the response once
     * received.
     *
     * @param returned The ReturnParameter object whose value is to be set to
     * the returned value.
     * @return
     */
    public Command handleResponse(ReturnParameter returned);

}
