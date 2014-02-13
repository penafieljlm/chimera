/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.model;

import ph.edu.dlsu.chimera.core.criteria.Criteria;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelSerializable implements Serializable {

    public final String protectedInterface;
    public final SubModel connectionSubModel;
    public final HashMap<String, SubModel> criteriaSubModels;

    public ModelSerializable(String protectedInterface, SubModel connectionSubModel, HashMap<String, SubModel> criteriaSubModels) {
        this.protectedInterface = protectedInterface;
        this.connectionSubModel = connectionSubModel;
        this.criteriaSubModels = criteriaSubModels;
    }

    public ModelSerializable(ModelLive live) {
        this.protectedInterface = live.protectedInterface;
        this.connectionSubModel = live.connectionSubModel;
        HashMap<String, SubModel> _criteriaTrees = new HashMap<String, SubModel>();
        for (Criteria crt : live.criteriaSubModels.keySet()) {
            _criteriaTrees.put(crt.expression, live.criteriaSubModels.get(crt));
        }
        this.criteriaSubModels = _criteriaTrees;
    }
}
