/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.reflection;

import de.tbsol.iptablesjava.rules.IpRule;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PacketField {

    public static final byte[] MASK_BYTE_32 = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    public static final InetAddress MASK_32 = InetAddress.getByAddress(PacketField.MASK_BYTE_32);
    public final Class headerClass;
    public final String fieldName;
    private final Constructor headerConstructor;

    //syntax: <header packages (optinal)>.<class name>.<field name>
    public PacketField(String expression) throws Exception {
        String[] e = expression.split("[.]");
        if (e.length < 2) {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
        String field = e[e.length - 1].trim();
        int hLastIdx = expression.lastIndexOf(field) - 1;
        if (hLastIdx < 0) {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
        String header = expression.substring(0, hLastIdx).trim();
        try {
            this.headerClass = Class.forName(header);
        } catch (Exception ex) {
            throw new Exception("Parse Error: header type '" + header + "', not found!");
        }
        if (this.headerClass != null) {
            if (JHeader.class.isAssignableFrom(this.headerClass)) {
                Constructor constr = null;
                try {
                    constr = this.headerClass.getConstructor();
                } catch (Exception ex) {
                    throw new Exception("Parse Error: constructor for header type '" + header + "', not found!");
                }
                if (constr != null) {
                    JHeader jh = null;
                    try {
                        jh = (JHeader) constr.newInstance();
                    } catch (Exception ex) {
                        throw new Exception("Parse Error: cannot create instance of header type '" + header + "'!");
                    }
                    if (jh != null) {
                        boolean found = false;
                        for (JField f : jh.getFields()) {
                            if (f.getName().equals(field)) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            this.headerConstructor = constr;
                        } else {
                            throw new Exception("Parse Error: field '" + field + "' not found on header type '" + header + "'!");
                        }
                    } else {
                        throw new Exception("Parse Error: cannot create instance of header type '" + header + "'!");
                    }
                } else {
                    throw new Exception("Parse Error: constructor for header type '" + header + "', not found!");
                }
            } else {
                throw new Exception("Parse Error: header type '" + header + "', not found!");
            }
        } else {
            throw new Exception("Parse Error: header type '" + header + "', not found!");
        }
        this.fieldName = field;
    }

    public BigInteger getFieldValue(PcapPacket pkt) {
        try {
            JHeader type = (JHeader) this.headerConstructor.newInstance();
            try {
                if (pkt.hasHeader(type)) {
                    JHeader inst = (JHeader) this.headerConstructor.newInstance();
                    JHeader jh = pkt.getHeader(inst);
                    for (JField f : jh.getFields()) {
                        if (f.getName().equals(this.fieldName)) {
                            byte[] value = jh.getByteArray(f.getOffset(jh) / 8, f.getLength(jh) / 8);
                            return new BigInteger(value);
                        }
                    }
                }
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public IpRule getRule(PcapPacket pkt) {
        IpRule rule = new IpRule();
        try {
            if (this.headerClass == Ip4.class) {
                rule.setProtocol(IpRule.IpProto.IPPROTO_ALL);
                switch (this.fieldName) {
                    case "source":
                        rule.setSource(Inet4Address.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setSourceMask(PacketField.MASK_32);
                    case "destination":
                        rule.setDestination(Inet4Address.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setDestinationMask(PacketField.MASK_32);
                }
                return rule;
            }
            if (this.headerClass == Icmp.class) {
                rule.setProtocol(IpRule.IpProto.IPPROTO_ICMP);
                return rule;
            }
            if (this.headerClass == Tcp.class) {
                switch (this.fieldName) {
                    case "source":
                        rule.setSource(Inet4Address.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setSourceMask(PacketField.MASK_32);
                        return rule;
                    case "destination":
                        rule.setDestination(Inet4Address.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setDestinationMask(PacketField.MASK_32);
                        return rule;
                }
            }
            if (this.headerClass == Udp.class) {
                switch (this.fieldName) {
                    case "source":
                        rule.setSource(Inet4Address.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setSourceMask(PacketField.MASK_32);
                        return rule;
                    case "destination":
                        rule.setDestination(Inet4Address.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setDestinationMask(PacketField.MASK_32);
                        return rule;
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }
}
