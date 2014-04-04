package ph.edu.dlsu.chimera.core.model;

import ph.edu.dlsu.chimera.core.criteria.Criteria;
import java.io.Serializable;
import java.util.HashMap;

/**
 * An instance of this class constitutes a network model which can be directly
 * serialized to a file.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelSerializable implements Serializable {

    /**
     * The name of the protected interface
     */
    public final String protectedInterface;
    /**
     * The connection tree and some supplementary data
     */
    public final SubModel connectionSubModel;
    /**
     * The criteria trees and some supplementary data
     */
    public final HashMap<String, SubModel> criteriaSubModels;

    /**
     * Constructs a new ModelSerializable object.
     *
     * @param protectedInterface The name of the protected interface
     * @param connectionSubModel The connection tree and some supplementary data
     * @param criteriaSubModels The criteria trees and some supplementary data
     */
    public ModelSerializable(String protectedInterface, SubModel connectionSubModel, HashMap<String, SubModel> criteriaSubModels) {
        this.protectedInterface = protectedInterface;
        this.connectionSubModel = connectionSubModel;
        this.criteriaSubModels = criteriaSubModels;
    }

    /**
     * Constructs a new ModelSerializable object using a ModelLive object.
     *
     * @param live The live model object
     */
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
