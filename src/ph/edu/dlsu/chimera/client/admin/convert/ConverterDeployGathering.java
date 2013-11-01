/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.constants.Defaults;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.MessageDeployGathering;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterDeployGathering extends CommandConverter {

    public ConverterDeployGathering() {
        super("Starts the Data Gathering deployment.");
        super.params.put("dumpFileName", "File name of the CSV file which will contain the training set.");
        super.params.put("ifExternal", "[OPTIONAL] Index of interface facing the external network (refer to the 'ifconfig' command).");
        super.params.put("ifInternal", "[OPTIONAL] Index of interface facing the internal network (refer to the 'ifconfig' command).");
        super.params.put("gatherAttacks", "[OPTIONAL] Marks the captured training set as an attack set (default: false).");
        super.params.put("statsTimeout", "[OPTIONAL] Expiration time of idle statistics instances in milliseconds (default:" + Defaults.DEFAULT_TIMEOUT_STATS_MS + "ms).");
        super.params.put("stateTimeout", "[OPTIONAL] Expiration time of idle tcp states instances in milliseconds (default:" + Defaults.DEFAULT_TIMEOUT_STATE_MS + "ms).");
    }

    @Override
    public Message toMessage(Command command) throws Exception {
        if (command.getKeys().size() < 1) {
            throw new Exception("The command needs at least 1 parameter: 'dumpFileName'.");
        }
        //dumpFileName
        String dumpFileName = command.getParameterValue("dumpFileName");
        if (dumpFileName == null) {
            throw new Exception("The parameter: 'dumpFileName' is missing.");
        }
        //external
        int ifExternal = -1;
        String _ifExternal = command.getParameterValue("ifExternal");
        if (_ifExternal != null) {
            try {
                ifExternal = Integer.valueOf(_ifExternal);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'ifExternal' must be a number.");
            }
        }
        //internal
        int ifInternal = -1;
        String _ifInternal = command.getParameterValue("ifInternal");
        if (_ifInternal != null) {
            try {
                ifInternal = Integer.valueOf(_ifInternal);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'ifInternal' must be a number.");
            }
        }
        //gatherAttacks
        boolean gatherAttacks = false;
        String _gatherAttacks = command.getParameterValue("gatherAttacks");
        if (_gatherAttacks != null) {
            try {
                gatherAttacks = Boolean.valueOf(_gatherAttacks);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'gatherAttacks' must be a boolean.");
            }
        }
        //statsTimeout
        long statsTimeout = Defaults.DEFAULT_TIMEOUT_STATS_MS;
        String _statsTimeout = command.getParameterValue("statsTimeout");
        if (_statsTimeout != null) {
            try {
                statsTimeout = Long.valueOf(_statsTimeout);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'statsTimeout' must be a number.");
            }
        }
        //stateTimeout
        long stateTimeout = Defaults.DEFAULT_TIMEOUT_STATE_MS;
        String _stateTimeout = command.getParameterValue("stateTimeout");
        if (_stateTimeout != null) {
            try {
                stateTimeout = Long.valueOf(_stateTimeout);
            } catch (NumberFormatException ex) {
                throw new Exception("The parameter: 'stateTimeout' must be a number.");
            }
        }
        //return
        return new MessageDeployGathering(ifExternal, ifInternal, dumpFileName, gatherAttacks, statsTimeout, stateTimeout);
    }
}
