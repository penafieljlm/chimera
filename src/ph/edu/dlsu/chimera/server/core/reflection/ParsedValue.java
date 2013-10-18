/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core.reflection;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ParsedValue {

    public final Object value;

    //value formats
    //ip:<ip-address>
    //dec:<decimal value>
    //hex:<hex value>
    public ParsedValue(String value) throws Exception {
        String v = value.toLowerCase();
        int boundaryIdx = v.indexOf("=");
        if (boundaryIdx < 0) {
            throw new Exception("Parse Error: Value type not found! Format is '<type><value>'!");
        }
        String type = v.substring(0, boundaryIdx);
        String val = v.substring(boundaryIdx + 1);
        switch (type) {
            case "ip":
                try {
                    this.value = InetAddress.getByName(val).getAddress();
                } catch (UnknownHostException ex) {
                    throw new Exception("Parse Error: Invalid IP Address '" + val + "'!");
                }
                break;
            case "dec":
                try {
                    this.value = Long.valueOf(val);
                } catch (NumberFormatException ex) {
                    throw new Exception("Parse Error: Value '" + val + "' is not a decimal number!");
                }
                break;
            case "hex":
                try {
                    this.value = Long.valueOf(val, 16);
                } catch (NumberFormatException ex) {
                    throw new Exception("Parse Error: Value '" + val + "' is not a hexadecimal number!");
                }
                break;
            default:
                throw new Exception("Parse Error: Unknown value type '" + type + "'");
        }
    }
}
