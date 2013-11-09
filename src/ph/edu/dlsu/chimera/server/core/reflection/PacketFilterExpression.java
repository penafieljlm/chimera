/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core.reflection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PacketFilterExpression extends PacketFilter {

    public final PacketFilter[] filters;
    public final OperationsBoolean[] operations;

    public PacketFilterExpression(String expression) throws Exception {
        String exp = expression;
        if (expression.startsWith("(") && expression.endsWith(")")) {
            exp = exp.substring(1, exp.length() - 1);
        }
        String[] ops = exp.split("([^!&|\\^()]+)|(\\(.+\\))");
        Pattern eptrn = Pattern.compile("([^!&|\\^()]+)|(\\(.+\\))");
        Matcher emtch = eptrn.matcher(exp);
        String[] exps = new String[emtch.groupCount()];
        for (int i = 0; i < exp.length(); i++) {
            exps[i] = emtch.group(i);
        }
        if (exps.length > 0) {
            if (ops.length != exps.length - 1) {
                throw new Exception("Parse Error: Syntax structure error in expression '" + exp + "'");
            }
            OperationsBoolean[] _operations = new OperationsBoolean[ops.length];
            for (int i = 0; i < ops.length; i++) {
                switch (ops[i].trim()) {
                    case "&&":
                        _operations[i] = OperationsBoolean.And;
                        break;
                    case "||":
                        _operations[i] = OperationsBoolean.Or;
                        break;
                    case "^^":
                        _operations[i] = OperationsBoolean.Xor;
                        break;
                    case "!&":
                        _operations[i] = OperationsBoolean.Nand;
                        break;
                    case "!|":
                        _operations[i] = OperationsBoolean.Nor;
                        break;
                    case "!^":
                        _operations[i] = OperationsBoolean.Xnor;
                        break;
                    default:
                        throw new Exception("Parse Error: Unknown operator '" + ops[i] + "'");
                }
            }
            PacketFilter[] _filters = new PacketFilter[exps.length];
            for (int i = 0; i < exps.length; i++) {
                _filters[i] = PacketFilter.parseExpression(exps[i].trim());
            }
            this.filters = _filters;
            this.operations = _operations;
        } else if (exps.length == 0 && ops.length == 0) {
            this.filters = null;
            this.operations = null;
        } else {
            throw new Exception("Parse Error: Syntax structure error in expression '" + exp + "'");
        }
    }

    @Override
    public boolean matches(PcapPacket pkt) {
        if (this.filters != null && this.operations != null) {
            if (this.filters.length > 0) {
                boolean result = this.filters[0].matches(pkt);
                for (int i = 1; i < this.filters.length; i++) {
                    boolean res = this.filters[1].matches(pkt);
                    switch (this.operations[i - 1]) {
                        case And:
                            result &= res;
                            continue;
                        case Or:
                            result |= res;
                            continue;
                        case Xor:
                            result ^= res;
                            continue;
                        case Nand:
                            result &= res;
                            result = !result;
                            continue;
                        case Nor:
                            result |= res;
                            result = !result;
                            continue;
                        case Xnor:
                            result ^= res;
                            result = !result;
                            continue;
                        default:
                            result = false;
                            continue;
                    }
                }
                return result;
            }
        }
        return true;
    }
}
