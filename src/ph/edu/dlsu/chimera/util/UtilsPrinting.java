/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.TrainingResult;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author AMD
 */
public abstract class UtilsPrinting {

    public static void printTrainingResult(TrainingResult result) throws Exception {
        UtilsPrinting.printModel(result.model, result.connectionInstances, result.criteriaInstances);
    }

    public static void printModel(ModelLive model, Instances connectionInstances, HashMap<Criteria, Instances> criteriaInstances) throws Exception {
        System.out.println("Interface....................... " + model.protectedInterface);
        if (model.connectionSubModel.tree != null) {
            UtilsPrinting.printTree("connection", model.connectionSubModel.tree, connectionInstances);
        }
        for (Criteria crt : criteriaInstances.keySet()) {
            UtilsPrinting.printTree(crt.expression, model.criteriaSubModels.get(crt).tree, criteriaInstances.get(crt));
        }
    }

    public static void printTree(String name, J48 tree, Instances data) throws Exception {
        System.out.println("Tree............................ " + name);
        System.out.println("    Number of Leaves............ " + tree.measureNumLeaves());
        System.out.println("    Size of the Tree............ " + tree.measureTreeSize());
        Evaluation eval = new Evaluation(data);
        double x[] = eval.evaluateModel(tree, data);
        System.out.println("    Evaluation Result........... ");
        int oneCount = 0;
        int zeroCount = 0;
        for (double d : x) {
            if (d == 1.0) {
                oneCount++;
            }
            if (d == 0.0) {
                zeroCount++;
            }
        }
        System.out.println("        Normal (1.0)............ " + oneCount);
        System.out.println("        Attack (0.0)............ " + zeroCount);
        System.out.print(eval.toSummaryString("    Summary of Training Set.....", false).replaceAll("\n", "\n        "));
        System.out.println("\b\b\b\bGraph....................... ");
        StringBuilder graphBuilder = new StringBuilder("        " + tree.graph().replaceAll("\n", "\n        "));
        for (int i = 0; i < 9; i++) {
            graphBuilder = graphBuilder.deleteCharAt(graphBuilder.length() - 1);
        }
        System.out.println(graphBuilder);
    }
}
