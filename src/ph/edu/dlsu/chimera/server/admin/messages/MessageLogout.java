package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 * An instance of this class constitutes a signal terminating a session between the server and a client.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageLogout implements ServerMessage {

    /**
     * Constructs a new MessageLogout object.
     */
    public MessageLogout() {
    }

    /**
     * Allows a server program to handle a Message object.
     * @param session - the session this Message object belongs to.
     * @param assembly - contains the components of the server.
     * @return null.
     * @throws Exception
     */
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        return null;
    }

}
