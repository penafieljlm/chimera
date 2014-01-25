/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.HashMap;
import java.util.List;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.TrainingResult;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.core.nic.NicData;
import ph.edu.dlsu.chimera.core.nic.NicDataAddress;
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
        System.out.println("        Attack (1.0)............ " + oneCount);
        System.out.println("        Normal (0.0)............ " + zeroCount);
        System.out.print(eval.toSummaryString("    Summary of Training Set.....", false).replaceAll("\n", "\n        "));
        System.out.println("\b\b\b\bGraph....................... ");
        StringBuilder graphBuilder = new StringBuilder("        " + tree.graph().replaceAll("\n", "\n        "));
        for (int i = 0; i < 9; i++) {
            graphBuilder = graphBuilder.deleteCharAt(graphBuilder.length() - 1);
        }
        System.out.println(graphBuilder);
    }

    public static void printDiagnostics(List diags, String prefix) {
        int maxNameLen = 0;
        for (Object o : diags) {
            if (o instanceof Diagnostic) {
                Diagnostic diag = (Diagnostic) o;
                if (diag.getName().length() > maxNameLen) {
                    maxNameLen = diag.getName().length();
                }
            }
        }
        maxNameLen += 2;
        for (Object o : diags) {
            if (o instanceof Diagnostic) {
                Diagnostic diag = (Diagnostic) o;
                if (diag.getValue() instanceof List) {
                    System.out.println(prefix + diag.getName());
                    UtilsPrinting.printDiagnostics((List<Diagnostic>) diag.getValue(), prefix + "    ");
                } else {
                    int dotCount = maxNameLen - diag.getName().length();
                    String dots = "";
                    for (int i = 0; i < dotCount; i++) {
                        dots += ".";
                    }
                    System.out.println(prefix + diag.getName() + dots + " " + diag.getValue());
                }
            }
        }
    }

    public static void printInterfaces(NicData[] cifaces) {
        System.out.println("CHIMERA Network Interfaces:");
        int intctr = 0;
        for (NicData nic : cifaces) {
            System.out.println("Interface................ " + intctr++);
            System.out.println("    Name................. " + nic.name);
            System.out.println("    Description.......... " + nic.description);
            System.out.println("    Hardware Address..... " + nic.hardwareAddress);
            int addrctr = 0;
            for (NicDataAddress addr : nic.addresses) {
                System.out.println("        Interface Address " + ++addrctr);
                System.out.println("            IP Address... " + addr.address);
                System.out.println("            Subnet Mask.. " + addr.netmask);
            }
        }
    }
}
