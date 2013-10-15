package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import ph.edu.dlsu.chimera.core.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 * An instance of this class constitutes a message reporting an exceptions.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageException implements ClientShellMessage {

    /**
     * The Exception carried by this MessageException object.
     */
    public final Exception exceptions;

    /**
     * Constructs a new MessageException object.
     * @param exceptions - the content of the new MessageObject to be created.
     */
    public MessageException(Exception exception) {
        this.exceptions = exception;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream) {
        outStream.println(this.exceptions.getMessage());
        return new MessageFinished();
    }

}
