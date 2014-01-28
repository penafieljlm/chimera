/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.reflection;

import java.lang.reflect.Constructor;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketFilterProtocol extends PacketFilter {

    public final Constructor headerConstructor;

    public PacketFilterProtocol(String expression) throws Exception {
        Class c = null;
        try {
            c = Class.forName(expression);
        } catch (Exception ex) {
            throw new Exception("Parse Error: header type '" + expression + "', not found!");
        }
        if (c != null) {
            if (JHeader.class.isAssignableFrom(c)) {
                Constructor constr = null;
                try {
                    constr = c.getConstructor();
                } catch (Exception ex) {
                    throw new Exception("Parse Error: constructor for header type '" + expression + "', not found!");
                }
                if (constr == null) {
                    throw new Exception("Parse Error: constructor for header type '" + expression + "', not found!");
                }
                this.headerConstructor = constr;
            } else {
                throw new Exception("Parse Error: header type '" + expression + "', not found!");
            }
        } else {
            throw new Exception("Parse Error: header type '" + expression + "', not found!");
        }
    }

    @Override
    public boolean matches(PcapPacket pkt) {
        try {
            JHeader type = (JHeader) this.headerConstructor.newInstance();
            try {
                return pkt.hasHeader(type);
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
        }
        return false;
    }
}
