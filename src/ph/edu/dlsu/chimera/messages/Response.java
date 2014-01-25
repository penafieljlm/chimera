package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a message which can be handled by a
 * client program.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface Response extends Message {

    public Command handleResponse(ReturnParameter returned);

}
