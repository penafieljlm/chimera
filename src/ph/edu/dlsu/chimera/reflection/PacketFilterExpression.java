package ph.edu.dlsu.chimera.reflection;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jnetpcap.packet.PcapPacket;

/**
 * An instance of this class constitutes a PacketFilter which deals with
 * compound PacketFilters (Packet Filters made out of multiple Packet Filters).
 * The PacketFilters within are separated by boolean operators.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PacketFilterExpression extends PacketFilter {

    /**
     * Sub packet filters
     */
    public final PacketFilter[] filters;
    /**
     * Operations between the filters; must be filters.length - 1 in length
     */
    public final OperationsBoolean[] operations;

    /**
     * Construct a new PacketFilterCondition using the provided expression
     * String which has the following format:
     * <exp-1><operation><exp-2><operation><exp-3>.
     *
     * @param expression The packet filter expression String
     * @throws Exception
     */
    public PacketFilterExpression(String expression) throws Exception {
        String exp = expression;
        if (expression.startsWith("(") && expression.endsWith(")")) {
            exp = exp.substring(1, exp.length() - 1);
        }
        String[] ops = exp.split("[ ]*(([^!&|\\^()]+)|(\\(.+\\)))[ ]*");
        if (!ops[0].isEmpty()) {
            throw new Exception("Parse Error: Syntax structure error in expression '" + exp + "'");
        }
        String[] _ops = new String[ops.length - 1];
        System.arraycopy(ops, 1, _ops, 0, _ops.length);
        ops = _ops;
        Pattern eptrn = Pattern.compile("[ ]*(([^!&|\\^()]+)|(\\(.+\\)))[ ]*");
        Matcher emtch = eptrn.matcher(exp);
        ArrayList<String> _exps = new ArrayList<String>();
        while (emtch.find()) {
            _exps.add(emtch.group().trim());
        }
        String[] exps = _exps.toArray(new String[0]);
        if (exps.length > 0) {
            if (ops.length != exps.length - 1) {
                throw new Exception("Parse Error: Syntax structure error in expression '" + exp + "'");
            }
            OperationsBoolean[] _operations = new OperationsBoolean[ops.length];
            for (int i = 0; i < ops.length; i++) {
                String op = ops[i].trim();
                if (op.equals("&&")) {
                    _operations[i] = OperationsBoolean.And;
                } else if (op.equals("||")) {
                    _operations[i] = OperationsBoolean.Or;
                } else if (op.equals("^^")) {
                    _operations[i] = OperationsBoolean.Xor;
                } else if (op.equals("!&")) {
                    _operations[i] = OperationsBoolean.Nand;
                } else if (op.equals("!|")) {
                    _operations[i] = OperationsBoolean.Nor;
                } else if (op.equals("!^")) {
                    _operations[i] = OperationsBoolean.Xnor;
                } else {
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
