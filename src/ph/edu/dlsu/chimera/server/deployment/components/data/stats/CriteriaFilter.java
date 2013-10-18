/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class CriteriaFilter {

    public final CriteriaSubject subject;
    public final String operator;
    public final String value;

    public CriteriaFilter(String header, String field, String operator, String value) {
        this.subject = new CriteriaSubject(header, field);
        this.operator = operator;
        this.value = value;
    }
}
