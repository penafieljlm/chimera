package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 * An instance of this class constitutes a message reporting an exception.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageException implements ClientShellMessage {

    /**
     * The Exception carried by this MessageException object.
     */
    public final Exception exception;

    /**
     * Constructs a new MessageException object.
     * @param exception - the content of the new MessageObject to be created.
     */
    public MessageException(Exception exception) {
        this.exception = exception;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream) {
        outStream.println(this.exception.getMessage());
        return new MessageFinished();
    }

}
