/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.server.admin.messages.MessageDeployDebugger;
import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.constants.ConstDefaults;
import ph.edu.dlsu.chimera.core.admin.messages.Message;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterDeployDebugger extends CommandConverter {

    public ConverterDeployDebugger() {
        super("Starts the Debug deployment.");
        super.params.put("ifInbound", "Name of interface facing the external network.");
        super.params.put("statsTimeout", "[OPTIONAL] Expiration time of idle statistics instances in milliseconds (default:" + ConstDefaults.DEFAULT_TIMEOUT_STATS_MS + "ms).");
        super.params.put("stateTimeout", "[OPTIONAL] Expiration time of idle tcp states instances in milliseconds (default:" + ConstDefaults.DEFAULT_TIMEOUT_STATE_MS + "ms).");
    }

    @Override
    public Message toMessage(Command command) throws Exception {
        if (command.getKeys().size() != 1) {
            throw new Exception("The command needs at least 1 parameter: 'ifInbound'!");
        }
        String ifInbound = command.getParameterValue("ifInbound");
        if (ifInbound == null) {
            throw new Exception("The parameter: 'ifInbound' is missing!");
        }
        long statsTimeout = ConstDefaults.DEFAULT_TIMEOUT_STATS_MS;
        String _statsTimeout = command.getParameterValue("statsTimeout");
        if (_statsTimeout != null) {
            try {
                statsTimeout = Long.valueOf(_statsTimeout);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'statsTimeout' must be a number!");
            }
        }
        long stateTimeout = ConstDefaults.DEFAULT_TIMEOUT_STATE_MS;
        String _stateTimeout = command.getParameterValue("stateTimeout");
        if (_stateTimeout != null) {
            try {
                stateTimeout = Long.valueOf(_stateTimeout);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'stateTimeout' must be a number!");
            }
        }
        return new MessageDeployDebugger(ifInbound, statsTimeout, stateTimeout);
    }
}
