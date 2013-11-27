/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.criteria;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class CriteriaInstance {

    public static final String CHAIN_PREFIX = "ccrtinstchain(";
    public static final String CHAIN_SUFFIX = ")";
    public final BigInteger[] criteriaId;
    public final Criteria criteria;

    public CriteriaInstance(BigInteger[] criteriaId, Criteria criteria) {
        this.criteriaId = criteriaId;
        this.criteria = criteria;
    }

    public String getStringId() {
        StringBuilder id = new StringBuilder("|");
        for (Object o : this.criteriaId) {
            if (o instanceof Object[]) {
                Object[] oba = (Object[]) o;
                for (Object obae : oba) {
                    id = id.append("[").append(obae.toString()).append("]");
                }
            } else {
                id = id.append(o.toString());
            }
        }
        return id.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CriteriaInstance other = (CriteriaInstance) obj;
        if (!Arrays.deepEquals(this.criteriaId, other.criteriaId)) {
            return false;
        }
        if (!Objects.equals(this.criteria, other.criteria)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Arrays.deepHashCode(this.criteriaId);
        hash = 67 * hash + Objects.hashCode(this.criteria);
        return hash;
    }

    public String getChainName() {
        StringBuilder id = new StringBuilder();
        for (BigInteger o : this.criteriaId) {
            id.append(o).append(".");
        }
        return CriteriaInstance.CHAIN_PREFIX + id.substring(0, id.length() - 1) + CriteriaInstance.CHAIN_SUFFIX;
    }
}
