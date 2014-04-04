package ph.edu.dlsu.chimera.util;

import java.util.HashMap;
import java.util.List;
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
 * This utility class provide functions dealing with printing.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsPrinting {

    /**
     * Prints the provided TrainingResult object onto the command line screen.
     *
     * @param result The TrainingResult object to be printed
     * @throws Exception
     */
    public static void printTrainingResult(TrainingResult result) throws Exception {
        UtilsPrinting.printModel(result.model, result.connectionInstances, result.criteriaInstances);
    }

    /**
     * Prints the provided ModelLive object onto the command line screen.
     *
     * @param model The ModelLive object to be printed
     * @param connectionInstances Connection instances used to conduct tests on
     * the model
     * @param criteriaInstances Criteria instances used to conduct tests on the
     * criteria set
     * @throws Exception
     */
    public static void printModel(ModelLive model, Instances connectionInstances, HashMap<Criteria, Instances> criteriaInstances) throws Exception {
        System.out.println("Interface....................... " + model.protectedInterface);
        if (model.connectionSubModel.tree != null) {
            UtilsPrinting.printTree("connection", model.connectionSubModel.tree, connectionInstances, model.connectionSubModel.attackClass);
        }
        for (Criteria crt : criteriaInstances.keySet()) {
            UtilsPrinting.printTree(crt.expression, model.criteriaSubModels.get(crt).tree, criteriaInstances.get(crt), model.criteriaSubModels.get(crt).attackClass);
        }
    }

    /**
     * Prints the provided tree using the provided data instances.
     *
     * @param name The tree name
     * @param tree The tree
     * @param data The testing data
     * @param attackClass The class which is considered an attack
     * @throws Exception
     */
    public static void printTree(String name, J48 tree, Instances data, double attackClass) throws Exception {
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
        int _atackcount = (attackClass == 1.0) ? oneCount : zeroCount;
        int _normalcount = (attackClass == 1.0) ? zeroCount : oneCount;
        System.out.println("        Attack.................. " + _atackcount);
        System.out.println("        Normal.................. " + _normalcount);
        System.out.print(eval.toSummaryString("    Summary of Training Set.....", false).replaceAll("\n", "\n        "));
        System.out.println("\b\b\b\bGraph....................... ");
        StringBuilder graphBuilder = new StringBuilder("    " + tree.graph().replaceAll("\n", "\n              "));
        for (int i = 0; i < 9; i++) {
            graphBuilder = graphBuilder.deleteCharAt(graphBuilder.length() - 1);
        }
        System.out.println(graphBuilder);
        System.out.println("        Java Code............... ");
        StringBuilder codeBuilder = new StringBuilder("    " + tree.toSource(name).replaceAll("\n", "\n              "));
        for (int i = 0; i < 9; i++) {
            codeBuilder = codeBuilder.deleteCharAt(graphBuilder.length() - 1);
        }
        System.out.println("        " + codeBuilder);
    }

    /**
     * Prints diagnostics. Recursive function.
     *
     * @param diags List of diagnostics
     * @param prefix Prefix for this iteration
     */
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

    /**
     * Prints the information from the provided NicData array.
     *
     * @param cifaces The NicDataArray
     */
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

    /**
     * Prints the contents of an array.
     *
     * @param a The array
     */
    public static void printArray(Object[] a) {
        StringBuilder b = new StringBuilder();
        for (Object c : a) {
            b = b.append("[").append(c).append("]");
        }
        System.out.println(b);
    }
}
