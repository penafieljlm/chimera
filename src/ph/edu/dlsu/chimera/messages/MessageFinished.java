package ph.edu.dlsu.chimera.messages;

import java.io.PrintStream;
import java.util.HashMap;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.messages.Command;
import ph.edu.dlsu.chimera.core.Session;

/**
 * An instance of this class constitutes a signal meaning that a host is finished communicating with its partner.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageFinished implements Response, Command {

    /**
     * Constructs a new MessageFinished object.
     */
    public MessageFinished() {
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response Command object.
     */
    public Command handleShellMessage(PrintStream outStream) {
        return new MessageBlank();
    }

    /**
     * Allows a server program to handle a Message object.
     * @param session - the session this Message object belongs to.
     * @param assembly - contains the components of the server.
     * @return a blank message.
     * @throws Exception
     */
    public Response handleMessage(Session session, HashMap<String, Component> assembly) throws Exception {
        return new MessageBlank();
    }
}
