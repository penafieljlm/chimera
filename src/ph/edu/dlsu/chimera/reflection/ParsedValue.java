/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.reflection;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ParsedValue {

    public final BigInteger value;

    //value formats
    //ip:<ip-address>
    //dec:<decimal value>
    //hex:<hex value>
    public ParsedValue(String value) throws Exception {
        String v = value.toLowerCase();
        int boundaryIdx = v.indexOf(":");
        if (boundaryIdx < 0) {
            throw new Exception("Parse Error: Invalid value type string '" + value + "'");
        }
        String type = v.substring(0, boundaryIdx);
        String val = v.substring(boundaryIdx + 1);
        if (type.equals("ip")) {
            try {
                this.value = new BigInteger(InetAddress.getByName(val).getAddress());
            } catch (UnknownHostException ex) {
                throw new Exception("Parse Error: Invalid IP Address '" + val + "'!");
            }
        } else if (type.equals("dec")) {
            try {
                this.value = new BigInteger(val);
            } catch (NumberFormatException ex) {
                throw new Exception("Parse Error: Value '" + val + "' is not a decimal number!");
            }
        } else if (type.equals("hex")) {
            try {
                this.value = new BigInteger(val, 16);
            } catch (NumberFormatException ex) {
                throw new Exception("Parse Error: Value '" + val + "' is not a hexadecimal number!");
            }
        } else {
            throw new Exception("Parse Error: Unknown value type '" + type + "'");
        }
    }
}
