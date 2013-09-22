package ph.edu.dlsu.chimera.admin.messages;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 * An instance of this class constitutes a signal meaning that a host is finished communicating with its partner.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageFinished implements ClientShellMessage, ServerMessage {

    /**
     * Constructs a new MessageFinished object.
     */
    public MessageFinished() {
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream) {
        return new MessageBlank();
    }

    /**
     * Allows a server program to handle a Message object.
     * @param session - the session this Message object belongs to.
     * @param assembly - contains the components of the server.
     * @return a blank message.
     * @throws Exception
     */
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        return new MessageBlank();
    }

}
