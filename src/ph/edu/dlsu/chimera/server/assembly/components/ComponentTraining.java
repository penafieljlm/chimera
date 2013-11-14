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
import java.util.Arrays;
import java.util.HashMap;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.core.InstanceManager;
import ph.edu.dlsu.chimera.core.ModelFile;
import ph.edu.dlsu.chimera.core.ModelLive;
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

    public ComponentTraining(File trainingFile, File modelFile, Criteria[] criterias) {
        this.trainingFile = trainingFile;
        this.modelFile = modelFile;
    }

    @Override
    protected void componentRun() throws Exception {
        //open training set file
        CSVReader reader = new CSVReader(new FileReader(this.trainingFile));
        //read criterias
        String[] _criterias = reader.readNext();
        Criteria[] criterias = new Criteria[_criterias.length];
        for (int i = 0; i < criterias.length; i++) {
            criterias[i] = new Criteria(_criterias[i]);
        }
        //create training subsets files
        File connectionDataSet = File.createTempFile("connection", ".trntmpcsv");
        HashMap<Criteria, File> criteriaDataSet = new HashMap<>();
        for (Criteria crt : criterias) {
            criteriaDataSet.put(crt, File.createTempFile("exp(" + crt.expression.replaceAll(" ", "") + ")", ".trntmpcsv"));
        }
        //open writers and write headers
        String[] attackHeader = {InstanceManager.ATTK_HEADER};
        CSVWriter connDataSetWriter = new CSVWriter(new FileWriter(connectionDataSet));
        connDataSetWriter.writeNext(ToolsArray.concat(InstanceManager.CORE_HEADERS, InstanceManager.CONN_HEADERS, attackHeader));
        HashMap<Criteria, CSVWriter> criteriaDataSetWriter = new HashMap<>();
        for (Criteria crt : criterias) {
            criteriaDataSetWriter.put(crt, new CSVWriter(new FileWriter(criteriaDataSet.get(crt))));
            criteriaDataSetWriter.get(crt).writeNext(ToolsArray.concat(InstanceManager.CORE_HEADERS, InstanceManager.getCriteriaHeaders(crt), attackHeader));
        }
        //get header
        String[] headers = reader.readNext();
        if (headers == null) {
            throw new Exception("Error: [Training] Missing headers.");
        }
        //per instance
        String[] instance = null;
        while ((instance = reader.readNext()) != null) {
            //get subinstances
            String[] core = InstanceManager.getCoreInstance(instance);
            String[] conn = InstanceManager.getConnectionInstance(instance);
            HashMap<Criteria, String[]> crts = new HashMap<>();
            for (Criteria crt : criterias) {
                crts.put(crt, InstanceManager.getCriteriaInstance(crt, headers, instance));
            }
            String[] attack = {instance[instance.length - 1]};
            //place core and attack header to subinstances
            conn = ToolsArray.concat(core, conn, attack);
            for (Criteria crt : criterias) {
                crts.put(crt, ToolsArray.concat(core, crts.get(crt), attack));
            }
            //write subinstances
            connDataSetWriter.writeNext(conn);
            connDataSetWriter.flush();
            for (Criteria crt : criterias) {
                criteriaDataSetWriter.get(crt).writeNext(crts.get(crt));
                criteriaDataSetWriter.get(crt).flush();
            }
        }
        //close writers
        connDataSetWriter.close();
        for (Criteria crt : criterias) {
            criteriaDataSetWriter.get(crt).close();
        }
        //create subset data sources
        DataSource connSource = new DataSource(new FileInputStream(connectionDataSet));
        HashMap<Criteria, DataSource> criteriaSource = new HashMap<>();
        for (Criteria crt : criterias) {
            criteriaSource.put(crt, new DataSource(new FileInputStream(criteriaDataSet.get(crt))));
        }
        //create subset instances
        Instances connInstance = connSource.getDataSet();
        HashMap<Criteria, Instances> criteriaInstance = new HashMap<>();
        for (Criteria crt : criterias) {
            criteriaInstance.put(crt, criteriaSource.get(crt).getDataSet());
        }
        //set class attributes
        if (connInstance.numAttributes() < 2) {
            if (connInstance.classIndex() == -1) {
                connInstance.setClassIndex(connInstance.numAttributes() - 1);
            }
        } else {
            throw new Exception("Error: [Training] Connection data set must have at least one custom attribute, and one class attribute.");
        }
        for (Criteria crt : criterias) {
            if (criteriaInstance.get(crt).numAttributes() < 2) {
                if (criteriaInstance.get(crt).classIndex() == -1) {
                    criteriaInstance.get(crt).setClassIndex(criteriaInstance.get(crt).numAttributes() - 1);
                }
            } else {
                throw new Exception("Error: [Training] Criteria data set must have at least one custom attribute, and one class attribute.");
            }
        }
        //set classifier options
        String[] options;
        try {
            options = weka.core.Utils.splitOptions(ComponentTraining.CLASSIFIER_OPTIONS);
        } catch (Exception ex) {
            throw new Exception("Error: [Training] Classifier options corrupted.");
        }
        //create trees
        J48 connTree = new J48();
        HashMap<Criteria, J48> criteriaTree = new HashMap<>();
        for (Criteria crt : criterias) {
            criteriaTree.put(crt, new J48());
        }
        //build trees
        try {
            connTree.buildClassifier(connInstance);
        } catch (Exception ex) {
            throw new Exception("Error: [Training] Cannot build classifier for connection tree.");
        }
        for (Criteria crt : criterias) {
            try {
                criteriaTree.get(crt).buildClassifier(criteriaInstance.get(crt));
            } catch (Exception ex) {
                throw new Exception("Error: [Training] Cannot build classifier for criteria tree.");
            }
        }
        //serialize tree
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(this.modelFile));
        } catch (IOException ex) {
            throw new Exception("Error: [Training] Cannot access modelFile.");
        }
        ModelFile _modelFile = new ModelFile(new ModelLive(connTree, criteriaTree));
        try {
            oos.writeObject(_modelFile);
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
