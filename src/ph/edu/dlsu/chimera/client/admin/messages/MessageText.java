package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.core.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 * An instance of this class constitutes a simple text message that can be sent between hosts.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageText implements ClientShellMessage {

    /**
     * The contents of this message.
     */
    public final String text;

    /**
     * Constructs a new MessageText object.
     * @param text - the contents of the new MessageText object to be created.
     */
    public MessageText(String text) {
        this.text = text;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream) {
        outStream.println(this.text);
        return new MessageFinished();
    }

}
