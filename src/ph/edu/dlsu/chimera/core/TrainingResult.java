/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import weka.core.Instances;

/**
 *
 * @author AMD
 */
public final class TrainingResult {

    public final ModelLive model;
    public final Instances connectionInstances;
    public final HashMap<Criteria, Instances> criteriaInstances;

    public TrainingResult(ModelLive model, Instances connectionInstances, HashMap<Criteria, Instances> criteriaInstances) {
        this.model = model;
        this.connectionInstances = connectionInstances;
        this.criteriaInstances = criteriaInstances;
    }
}
