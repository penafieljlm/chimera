/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.model;

import ph.edu.dlsu.chimera.core.criteria.Criteria;
import java.util.HashMap;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelLive {

    public final String protectedInterface;
    public final SubModel connectionSubModel;
    public final HashMap<Criteria, SubModel> criteriaSubModels;

    public ModelLive(String protectedInterface, SubModel connectionSubModel, HashMap<Criteria, SubModel> criteriaSubModels) {
        this.protectedInterface = protectedInterface;
        this.connectionSubModel = connectionSubModel;
        this.criteriaSubModels = criteriaSubModels;
    }

    public ModelLive(ModelSerializable file) throws Exception {
        this.protectedInterface = file.protectedInterface;
        this.connectionSubModel = file.connectionSubModel;
        HashMap<Criteria, SubModel> _criteriaTrees = new HashMap<Criteria, SubModel>();
        for (String crt : file.criteriaSubModels.keySet()) {
            _criteriaTrees.put(new Criteria(crt), file.criteriaSubModels.get(crt));
        }
        this.criteriaSubModels = _criteriaTrees;
    }
}
