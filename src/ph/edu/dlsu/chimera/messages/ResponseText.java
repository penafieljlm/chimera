package ph.edu.dlsu.chimera.messages;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Client;

/**
 * An instance of this class constitutes a simple text message that can be sent between hosts.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseText implements Response {

    /**
     * The contents of this message.
     */
    public final String text;

    /**
     * Constructs a new ResponseText object.
     * @param text - the contents of the new ResponseText object to be created.
     */
    public ResponseText(String text) {
        this.text = text;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response Command object.
     */
    public Command handleShellMessage(PrintStream outStream) {
        outStream.println(this.text);
        return new MessageFinished();
    }

}
