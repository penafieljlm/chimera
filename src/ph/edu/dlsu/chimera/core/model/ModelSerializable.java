/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.model;

import ph.edu.dlsu.chimera.core.criteria.Criteria;
import java.io.Serializable;
import java.util.HashMap;
import weka.classifiers.trees.J48;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelSerializable implements Serializable {

    public final J48 connectionTree;
    public final HashMap<String, J48> criteriaTrees;

    public ModelSerializable(J48 connectionTree, HashMap<String, J48> criteriaTrees) {
        this.connectionTree = connectionTree;
        this.criteriaTrees = criteriaTrees;
    }

    public ModelSerializable(ModelLive live) {
        this.connectionTree = live.connectionTree;
        HashMap<String, J48> _criteriaTrees = new HashMap<>();
        for (Criteria crt : live.criteriaTrees.keySet()) {
            _criteriaTrees.put(crt.expression, live.criteriaTrees.get(crt));
        }
        this.criteriaTrees = _criteriaTrees;
    }
}
