/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.util.HashMap;
import weka.classifiers.trees.J48;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelLive {

    public final J48 connectionTree;
    public final HashMap<Criteria, J48> criteriaTrees;

    public ModelLive(J48 connectionTree, HashMap<Criteria, J48> criteriaTrees) {
        this.connectionTree = connectionTree;
        this.criteriaTrees = criteriaTrees;
    }

    public ModelLive(ModelSerializable file) throws Exception {
        this.connectionTree = file.connectionTree;
        HashMap<Criteria, J48> _criteriaTrees = new HashMap<>();
        for (String crt : file.criteriaTrees.keySet()) {
            _criteriaTrees.put(new Criteria(crt), file.criteriaTrees.get(crt));
        }
        this.criteriaTrees = _criteriaTrees;
    }
}
