package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.Command;

/**
 * An instance of this class constitutes a message which can be handled by a client program.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public interface Response extends Message {

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response Command object.
     */
    public Command handleShellMessage(PrintStream outStream);

}
