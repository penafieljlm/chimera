/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import ph.edu.dlsu.chimera.core.Criteria;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentTraining extends ComponentActive {

    public static String CLASSIFIER_OPTIONS = "-C 0.25 -M 2";
    public final File trainingFile;
    public final File modelFile;
    public final Criteria[] criterias;

    public ComponentTraining(File trainingFile, File modelFile, Criteria[] criterias) {
        this.trainingFile = trainingFile;
        this.modelFile = modelFile;
        this.criterias = criterias;
    }

    @Override
    protected void componentRun() throws Exception {

        //read data source
        DataSource source = null;
        try {
            source = new DataSource(new FileInputStream(this.trainingFile));
        } catch (FileNotFoundException ex) {
            throw new Exception("Error: [Training] traningFile not found.");
        }
        Instances data;
        try {
            data = source.getDataSet();
        } catch (Exception ex) {
            throw new Exception("Error: [Training] Cannot get data from source.");
        }
        //set class attribute
        if (data.numAttributes() < 2) {
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
        } else {
            throw new Exception("Error: [Training] Data set must have at least one custom attribute, and one class attribute.");
        }
        String[] options;
        try {
            options = weka.core.Utils.splitOptions(ComponentTraining.CLASSIFIER_OPTIONS);
        } catch (Exception ex) {
            throw new Exception("Error: [Training] Classifier options corrupted.");
        }
        J48 tree = new J48();
        try {
            tree.setOptions(options);
        } catch (Exception ex) {
            throw new Exception("Error: [Training] Unsupported option detected.");
        }
        try {
            tree.buildClassifier(data);
        } catch (Exception ex) {
            throw new Exception("Error: [Training] Cannot build classifier.");
        }
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(this.modelFile));
        } catch (IOException ex) {
            throw new Exception("Error: [Training] Cannot access modelFile.");
        }
        try {
            oos.writeObject(tree);
        } catch (IOException ex) {
            throw new Exception("Error: [Training] Cannot write to modelFile.");
        }
        try {
            oos.flush();
        } catch (IOException ex) {
            throw new Exception("Error: [Training] Cannot flush to modelFile.");
        }
        try {
            oos.close();
        } catch (IOException ex) {
            throw new Exception("Error: [Training] Cannot close modelFile.");
        }
    }

    public void testClassifier(J48 tree, Instances data) throws Exception {
        System.out.println("\nNumber of Leaves: " + tree.measureNumLeaves());
        System.out.println("\nSize of the Tree: " + tree.measureTreeSize());
        //decision start
        Evaluation eval = new Evaluation(data);
        double x[] = eval.evaluateModel(tree, data);
        System.out.println(eval.toSummaryString("\n== Evaluation on Training Set ==\n", false));

        System.out.println("Eval result");
        int oneCount = 0;
        int zeroCount = 0;
        for (double d : x) {
            if (d == 1.0) {
                oneCount++;
            }
            if (d == 0.0) {
                zeroCount++;
            }
            System.out.println(d);
        }
        System.out.println("ONE: " + oneCount);
        System.out.println("ZERO: " + zeroCount);
        System.out.println(tree.graph());
    }
}