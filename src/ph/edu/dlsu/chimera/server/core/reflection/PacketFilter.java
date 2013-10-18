/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core.reflection;

import org.jnetpcap.packet.PcapPacket;

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
        return this.subject.getFieldValue(pkt).equals(this.value.value);
    }
}
