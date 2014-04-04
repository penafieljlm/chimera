package ph.edu.dlsu.chimera.core.criteria;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * An instance of this class constitutes the values associated with the packet
 * fields enumerated in a Criteria object.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class CriteriaInstance {

    private static final String CHAIN_PREFIX = "ccrtinstchain(";
    private static final String CHAIN_SUFFIX = ")";
    /**
     * The set of identifying values associated with the packet fields
     * enumerated in the Criteria object
     */
    public final BigInteger[] criteriaId;
    /**
     * The Criteria object which this CriteriaInstance object is associated with
     */
    public final Criteria criteria;

    /**
     * Constructs a new CriteriaInstance object.
     *
     * @param criteriaId The set of identifying values associated with the
     * packet fields enumerated in the Criteria object
     * @param criteria The Criteria object which this CriteriaInstance object is
     * associated with
     */
    public CriteriaInstance(BigInteger[] criteriaId, Criteria criteria) {
        this.criteriaId = criteriaId;
        this.criteria = criteria;
    }

    /**
     *
     * @return A String representation of the criteriaId.
     */
    public String getStringId() {
        StringBuilder id = new StringBuilder();
        for (BigInteger o : this.criteriaId) {
            id = id.append("[").append(o.toString(16)).append("]");
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
}
