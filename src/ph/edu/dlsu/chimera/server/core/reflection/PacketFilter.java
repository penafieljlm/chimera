/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core.reflection;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PacketFilter {

    public final PacketField subject;
    public final ParsedValue value;

    //syntax: <field>=<value>
    public PacketFilter(String expression) throws Exception {
        String[] e = expression.split("=");
        if (e.length != 2) {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
        this.subject = new PacketField(e[0].trim());
        this.value = new ParsedValue(e[1].trim());
    }

    public boolean matches(PcapPacket pkt) throws Exception {
        Object val = this.subject.getFieldValue(pkt);
        if (val != null) {
            if (val instanceof Number && this.value.value instanceof Number) {
                Number x = (Number) val;
                Number y = (Number) this.value.value;
                if (x.doubleValue() == y.doubleValue()) {
                    return true;
                }
                if (x.longValue() == y.longValue()) {
                    return true;
                }
            }
            return this.value.value.equals(val);
        }
        return false;
    }
}
