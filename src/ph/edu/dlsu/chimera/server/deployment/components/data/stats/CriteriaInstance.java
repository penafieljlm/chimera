/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class CriteriaInstance {

    public final String criteriaId;
    public final Criteria criteria;

    public CriteriaInstance(String criteriaId, Criteria criteria) {
        this.criteriaId = criteriaId;
        this.criteria = criteria;
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
        if ((this.criteriaId == null) ? (other.criteriaId != null) : !this.criteriaId.equals(other.criteriaId)) {
            return false;
        }
        if (this.criteria != other.criteria && (this.criteria == null || !this.criteria.equals(other.criteria))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.criteriaId != null ? this.criteriaId.hashCode() : 0);
        hash = 83 * hash + (this.criteria != null ? this.criteria.hashCode() : 0);
        return hash;
    }
}
