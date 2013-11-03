package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.Response;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 * An instance of this class constitutes a message which can be handled by a server program.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface Command extends Message {

    /**
     * Allows a server program to handle a Message object.
     * Invoked by the server.
     * @param session - the session this Message object belongs to.
     * @param assembly - contains the components of the server.
     * @return the appropriate response.
     * @throws Exception
     */
    public Response handleMessage(Session session, Assembly assembly) throws Exception;
    
}
