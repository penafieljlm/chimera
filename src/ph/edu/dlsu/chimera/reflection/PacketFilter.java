/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.reflection;

import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PacketFilter {

    public static PacketFilter parseExpression(String expression) throws Exception {
        if (expression.contains("&&")
                || expression.contains("||")
                || expression.contains("^^")
                || expression.contains("!&")
                || expression.contains("!|")
                || expression.contains("!&")) {
            return new PacketFilterExpression(expression);
        }
        if (expression.contains("==")
                || expression.contains("!=")
                || expression.contains("<=")
                || expression.contains(">=")
                || expression.contains("<")
                || expression.contains(">")) {
            return new PacketFilterCondition(expression);
        }
        return new PacketFilterProtocol(expression);
    }

    public abstract boolean matches(PcapPacket pkt);
}
