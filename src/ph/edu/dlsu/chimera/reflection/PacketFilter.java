package ph.edu.dlsu.chimera.reflection;

import org.jnetpcap.packet.PcapPacket;

/**
 * An instance of this class constitutes an object to which packets could be
 * matched against.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PacketFilter {

    /**
     * Parses the provided filter expression.
     *
     * @param expression The filter expression
     * @return The parsed packet filter object
     * @throws Exception
     */
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

    /**
     * Matches the given packet to this PacketFilter.
     *
     * @param pkt The given packet
     * @return True if the given packet matches this PacketFilter
     */
    public abstract boolean matches(PcapPacket pkt);
}
