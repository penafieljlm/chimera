package ph.edu.dlsu.chimera.rules;

/**
 * This class constitutes information about iptables rules placed on a chain.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class RuleInfo {

    /**
     * The id identifying the rule
     */
    public final Object id;
    /**
     * The time that the rule was created
     */
    public final long timeCreatedMs;

    /**
     * Constructs a new RuleInfo object.
     *
     * @param id The id identifying the rule
     * @param timeCreated The time that the rule was created
     */
    public RuleInfo(Object id, long timeCreated) {
        this.id = id;
        this.timeCreatedMs = timeCreated;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final RuleInfo other = (RuleInfo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
