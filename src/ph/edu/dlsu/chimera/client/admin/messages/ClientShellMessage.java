package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 * An instance of this class constitutes a message which can be handled by a client program.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface ClientShellMessage extends Message {

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream);

}
