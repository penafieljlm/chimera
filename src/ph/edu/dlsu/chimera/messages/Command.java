package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.core.Session;

/**
 * An instance of this interface constitutes a command message used by remote
 * clients to control the system in deployment.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface Command extends Message {

    /**
     * Server side function which executes the contents of the command once
     * received.
     *
     * @param session The session which the command belongs to
     * @param assembly The component assembly of the deployed system
     * @return The appropriate Response object which is to be sent back the
     * client
     * @throws Exception
     */
    public Response handleCommand(Session session, HashMap<String, Component> assembly) throws Exception;
}
