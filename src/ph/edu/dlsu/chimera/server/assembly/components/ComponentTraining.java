/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.core.InstanceManager;
import ph.edu.dlsu.chimera.util.ToolsArray;
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
        CSVReader reader = new CSVReader(new FileReader(this.modelFile));
        File connectionDataSet = File.createTempFile("connection", ".trntmpcsv");
        HashMap<Criteria, File> criteriaDataSets = new HashMap<>();
        for (Criteria crt : this.criterias) {
            criteriaDataSets.put(crt, File.createTempFile("exp(" + crt.expression.replaceAll(" ", "") + ")", ".trntmpcsv"));
        }
        CSVWriter connDataSetWriter = new CSVWriter(new FileWriter(connectionDataSet));
        connDataSetWriter.writeNext(ToolsArray.concat(InstanceManager.CORE_HEADERS, InstanceManager.CONN_HEADERS));
        HashMap<Criteria, CSVWriter> criteriaDataSetWriter = new HashMap<>();
        for (Criteria crt : this.criterias) {
            criteriaDataSetWriter.put(crt, new CSVWriter(new FileWriter(criteriaDataSets.get(crt))));
            criteriaDataSetWriter.get(crt).writeNext(ToolsArray.concat(InstanceManager.CORE_HEADERS, InstanceManager.getCriteriaHeaders(crt)));
        }
        String[] headers = reader.readNext();
        if (headers == null) {
            throw new Exception("Error: [Training] Missing headers.");
        }
        String[] instance = null;
        while ((instance = reader.readNext()) != null) {
            String[] core = InstanceManager.getCoreInstance(instance);
            String[] conn = InstanceManager.getConnectionInstance(instance);
            HashMap<Criteria, String[]> crts = new HashMap<>();
            for (Criteria crt : this.criterias) {
                crts.put(crt, InstanceManager.getCriteriaInstance(crt, headers, instance));
            }

        }

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
