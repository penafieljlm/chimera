/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author Emerson Chua
 */
public class TrainingSample {

    public static String instanceFilePath = "C:/Users/Fili Emerson Chua/Dropbox/DLSU Shared/CHIMERA/Proof of Concept/redefensedataset.csv";
    public static String classifierOptions = "-C 0.25 -M 2";
    public static String modelFile = "C:/Users/Fili Emerson Chua/Dropbox/DLSU Shared/CHIMERA/Proof of Concept/baseline.model";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //read data source
            DataSource source = new DataSource(instanceFilePath);
            Instances data = source.getDataSet();
            
            //set class attribute
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
            
            String[] options = weka.core.Utils.splitOptions(classifierOptions);
            
            J48 tree = new J48();
            tree.setOptions(options);
            tree.buildClassifier(data);
            
            System.out.println("\nNumber of Leaves: " + tree.measureNumLeaves());
            System.out.println("\nSize of the Tree: " + tree.measureTreeSize());
            
            Evaluation eval = new Evaluation(data);
            double x[] = eval.evaluateModel(tree, data);
            System.out.println(eval.toSummaryString("\n== Evaluation on Training Set ==\n", false));
            
            System.out.println("Eval result");
            int oneCount = 0;
            int zeroCount = 0;
            for(double d : x) {
                if(d == 1.0)
                    oneCount++;
                if(d == 0.0)
                    zeroCount++;
                System.out.println(d);
            }
            System.out.println("ONE: " + oneCount);
            System.out.println("ZERO: " + zeroCount);
            
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(modelFile));
            
            oos.writeObject(tree);
            oos.flush();
            oos.close();
            
            System.out.println(tree.graph());
            
        } catch (Exception ex) {
            Logger.getLogger(TrainingSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
//    // deserialize model
//    ObjectInputStream ois = new ObjectInputStream(
//                              new FileInputStream("/some/where/j48.model"));
//    Classifier cls = (Classifier) ois.readObject();
//    ois.close();
}
