/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.server.admin.messages.MessageQueryError;
import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.core.admin.messages.Message;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterQueryError extends CommandConverter {

    public ConverterQueryError() {
        super("Retrieves and prints the exceptions encountered by the specified component.");
        super.params.put("component", "Name of component to query for exceptions.");
        super.params.put("printStackTrace", "[OPTIONAL] Signals if the component's stack trace will be printed (default: false).");
        super.params.put("pollErrors", "[OPTIONAL] Signals if the component's errors list will be cleared upon querying. (default: true).");
    }

    public Message toMessage(Command command) throws Exception {
        if (command.getKeys().size() < 1) {
            throw new Exception("The command needs at least 1 parameter: 'component'!");
        }
        String component = command.getParameterValue("component");
        if (component == null) {
            throw new Exception("The parameter: 'component' is missing!");
        }
        boolean printStackTrace = false;
        String _printStackTrace = command.getParameterValue("printStackTrace");
        if (_printStackTrace != null) {
            if (_printStackTrace.equals("true")) {
                printStackTrace = true;
            } else if (_printStackTrace.equals("false")) {
                printStackTrace = false;
            } else {
                throw new Exception("The parameter: 'printStackTrace' must be 'true' or 'false'!");
            }
        }
        boolean pollErrors = true;
        String _pollErrors = command.getParameterValue("pollErrors");
        if (_pollErrors != null) {
            if (_pollErrors.equals("true")) {
                pollErrors = true;
            } else if (_pollErrors.equals("false")) {
                pollErrors = false;
            } else {
                throw new Exception("The parameter: 'pollErrors' must be 'true' or 'false'!");
            }
        }
        return new MessageQueryError(component, printStackTrace, pollErrors);
    }
}
