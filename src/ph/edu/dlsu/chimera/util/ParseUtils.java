/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.text.ParseException;
import java.util.HashMap;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ParseUtils {

    public static HashMap<String, String> parseArgs(String[] args) throws ParseException {
        HashMap<String, String> _args = new HashMap<>();
        String varname = null;
        for (int i = 0; i < args.length; i++) {
            if (varname == null && args[i].startsWith("/")) {
                _args.put(args[i], "true");
                continue;
            }
            if (varname == null && args[i].startsWith("-")) {
                varname = args[i];
                continue;
            }
            if (varname != null) {
                _args.put(varname, args[i]);
                varname = null;
                continue;
            }
            throw new ParseException("Expected variable name.", i);
        }
        return _args;
    }
}
