package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a Response containing a simple text
 * message that can be sent between hosts.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseText implements Response {

    /**
     * The simple text message
     */
    public final String text;

    /**
     * Constructs a new ResponseText object.
     *
     * @param text The simple text message
     */
    public ResponseText(String text) {
        this.text = text;
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        returned.setReturnedObject(this.text);
        return new MessageFinished();
    }

}
