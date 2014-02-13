/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.rules;

/**
 *
 * @author Nikkol
 */
public class RuleInfo {

    public final Object id;
    public final long timeCreatedMs;

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
