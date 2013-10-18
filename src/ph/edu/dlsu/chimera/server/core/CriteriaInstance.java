/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class CriteriaInstance {

    public final Object[] criteriaId;
    public final Criteria criteria;

    public CriteriaInstance(Object[] criteriaId, Criteria criteria) {
        this.criteriaId = criteriaId;
        this.criteria = criteria;
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
}
