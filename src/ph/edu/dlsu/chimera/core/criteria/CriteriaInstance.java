/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.criteria;

import java.math.BigInteger;
import java.util.Arrays;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Arrays.deepHashCode(this.criteriaId);
        hash = 89 * hash + (this.criteria != null ? this.criteria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CriteriaInstance other = (CriteriaInstance) obj;
        if (!Arrays.deepEquals(this.criteriaId, other.criteriaId)) {
            return false;
        }
        if (this.criteria != other.criteria && (this.criteria == null || !this.criteria.equals(other.criteria))) {
            return false;
        }
        return true;
    }

    public String getChainName() {
        StringBuilder id = new StringBuilder();
        for (BigInteger o : this.criteriaId) {
            id.append(o).append(".");
        }
        return CriteriaInstance.CHAIN_PREFIX + id.substring(0, id.length() - 1) + CriteriaInstance.CHAIN_SUFFIX;
    }
}
