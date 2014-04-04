package ph.edu.dlsu.chimera;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.core.model.ModelSerializable;
import ph.edu.dlsu.chimera.util.UtilsParse;
import weka.core.Instance;
import weka.core.Instances;

/**
 * The cmtest class contains the entry point for the cmtest command.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cmtest {

    public static final String USAGE = ""
            + "\nCHIMERA Model Test Command Usage (cmtest)"
            + "\nDescription:"
            + "\n    The cmtest command allows users to manually test a CHIMERA model."
            + "\nSyntax:"
            + "\n    cmtest (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -input"
            + "\n        DESCRIPTION"
            + "\n            The input file name of the model to be used."
            + "\n            Automatically ends with '.cmodel'."
            + "\n        REQUIRED........ Yes";

    public static void main(String[] args) throws Exception {
        try {
            //help
            if (args.length == 1) {
                if (args[0].equals("/help")) {
                    System.out.println(USAGE);
                    return;
                }
            }

            //parse args
            HashMap<String, String> _args = UtilsParse.parseArgs(args);

            //load model file
            String modelFile = _args.get("-input");

            //load model file
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(modelFile + ".cmodel"));
            ModelSerializable modelSerializable = (ModelSerializable) objectInputStream.readObject();
            ModelLive modelLive = new ModelLive(modelSerializable);

            //create data sets definitions
            Instances connInstances = new Instances("connInstances", modelLive.connectionSubModel.attributes, 0);
            connInstances.setClassIndex(connInstances.numAttributes() - 1);
            HashMap<Criteria, Instances> crtInstances = new HashMap<Criteria, Instances>();
            for (Criteria crt : modelLive.criteriaSubModels.keySet()) {
                crtInstances.put(crt, new Instances(crt.expression, modelLive.criteriaSubModels.get(crt).attributes, 0));
                crtInstances.get(crt).setClassIndex(crtInstances.get(crt).numAttributes() - 1);
            }

            //query connection tuple
            StringBuilder connHeaders = new StringBuilder();
            for (int i = 0; i < connInstances.numAttributes(); i++) {
                connHeaders = connHeaders.append("|").append(connInstances.attribute(i).name()).append("|");
            }
            System.out.println(connHeaders);
            String[] connTuple = System.console().readLine().split(",");
            if (!connTuple[0].equals("\\skip")) {
                //test connection tree
                Instance connInstance = new Instance(connInstances.numAttributes());
                connInstance.setDataset(connInstances);
                for (int i = 0; i < connInstances.numAttributes() && i < connTuple.length; i++) {
                    if (connInstances.attribute(i).isNumeric()) {
                        connInstance.setValue(i, Double.parseDouble(connTuple[i]));
                    } else {
                        connInstance.setValue(i, connTuple[i]);
                    }
                }
                connInstance.setMissing(connInstance.numAttributes() - 1);
                double eval = modelLive.connectionSubModel.tree.classifyInstance(connInstance);
                System.out.println("Attack?:" + (eval == modelLive.connectionSubModel.attackClass));
            } else {
                System.out.println("Skipped...");
            }

            //query criteria tuples
            for (Criteria crt : modelLive.criteriaSubModels.keySet()) {
                StringBuilder crtHeaders = new StringBuilder();
                for (int i = 0; i < crtInstances.get(crt).numAttributes(); i++) {
                    crtHeaders = crtHeaders.append("|").append(crtInstances.get(crt).attribute(i).name()).append("|");
                }
                System.out.println(crtHeaders);
                String[] crtTuple = System.console().readLine().split(",");
                if (!crtTuple[0].equals("\\skip")) {
                    //test criteria trees
                    Instance crtInstance = new Instance(crtInstances.get(crt).numAttributes());
                    crtInstance.setDataset(crtInstances.get(crt));
                    for (int i = 0; i < crtInstances.get(crt).numAttributes() && i < crtTuple.length; i++) {
                        if (crtInstances.get(crt).attribute(i).isNumeric()) {
                            crtInstance.setValue(i, Double.parseDouble(crtTuple[i]));
                        } else {
                            crtInstance.setValue(i, crtTuple[i]);
                        }
                    }
                    crtInstance.setMissing(crtInstance.numAttributes() - 1);
                    double eval = modelLive.criteriaSubModels.get(crt).tree.classifyInstance(crtInstance);
                    System.out.println("Attack?:" + (eval == modelLive.criteriaSubModels.get(crt).attackClass));
                } else {
                    System.out.println("Skipped...");
                }
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cmtest /help' to see usage.");
        }
    }

}
