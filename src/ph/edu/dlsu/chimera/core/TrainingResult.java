package ph.edu.dlsu.chimera.core;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import weka.core.Instances;

/**
 * An instance of this class constitutes the result of the training process.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class TrainingResult {

    /**
     * The generated model
     */
    public final ModelLive model;
    /**
     * An object describing the attributes that the connection tree (in the
     * model) accepts
     */
    public final Instances connectionInstances;
    /**
     * A list of objects describing the attributes that the criteria trees (in
     * the model) accepts
     */
    public final HashMap<Criteria, Instances> criteriaInstances;

    /**
     * Constructs a new TrainingResult object.
     *
     * @param model The model created by the training process
     * @param connectionInstances An object describing the attributes that the
     * connection tree (in the model) accepts
     * @param criteriaInstances A list of objects describing the attributes that
     * the criteria trees (in the model) accepts
     */
    public TrainingResult(ModelLive model, Instances connectionInstances, HashMap<Criteria, Instances> criteriaInstances) {
        this.model = model;
        this.connectionInstances = connectionInstances;
        this.criteriaInstances = criteriaInstances;
    }

}
