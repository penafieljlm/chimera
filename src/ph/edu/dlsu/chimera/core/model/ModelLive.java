package ph.edu.dlsu.chimera.core.model;

import ph.edu.dlsu.chimera.core.criteria.Criteria;
import java.util.HashMap;

/**
 * An instance of this class constitutes a network model which can be directly
 * used by the system.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ModelLive {

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
    public final HashMap<Criteria, SubModel> criteriaSubModels;

    /**
     * Constructs a new ModelLive object.
     *
     * @param protectedInterface The name of the protected interface
     * @param connectionSubModel The connection tree and some supplementary data
     * @param criteriaSubModels The criteria trees and some supplementary data
     */
    public ModelLive(String protectedInterface, SubModel connectionSubModel, HashMap<Criteria, SubModel> criteriaSubModels) {
        this.protectedInterface = protectedInterface;
        this.connectionSubModel = connectionSubModel;
        this.criteriaSubModels = criteriaSubModels;
    }

    /**
     * Constructs a new ModelLive object using a ModelSerializable object.
     *
     * @param file The serializable model object directly extracted from a file
     * @throws Exception
     */
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
