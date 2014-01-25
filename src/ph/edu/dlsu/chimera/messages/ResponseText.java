package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 * An instance of this class constitutes a simple text message that can be sent
 * between hosts.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseText implements Response {

    public final String text;

    public ResponseText(String text) {
        this.text = text;
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        returned.setReturnedObject(this.text);
        return new MessageFinished();
    }

}
