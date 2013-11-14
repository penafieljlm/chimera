/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.io.Serializable;
import java.util.HashMap;
import weka.classifiers.trees.J48;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelFile implements Serializable {

    public final J48 connectionTree;
    public final HashMap<String, J48> criteriaTrees;

    public ModelFile(J48 connectionTree, HashMap<String, J48> criteriaTrees) {
        this.connectionTree = connectionTree;
        this.criteriaTrees = criteriaTrees;
    }

    public ModelFile(ModelLive live) {
        this.connectionTree = live.connectionTree;
        HashMap<String, J48> _criteriaTrees = new HashMap<>();
        for (Criteria crt : live.criteriaTrees.keySet()) {
            _criteriaTrees.put(crt.expression, live.criteriaTrees.get(crt));
        }
        this.criteriaTrees = _criteriaTrees;
    }
}
