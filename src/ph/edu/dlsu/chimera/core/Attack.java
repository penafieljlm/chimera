/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Administrator
 */
public class Attack {

    public final String base;

    public Attack(String base) {
        this.base = base;
    }

    public int simulate(HashMap<String, String> parameters) throws Exception {
        String cmd = this.base;
        for (String parameter : parameters.keySet()) {
            cmd = cmd.replaceAll("{" + parameter + "}", parameters.get(parameter));
        }
        Process prc = Runtime.getRuntime().exec(cmd);
        return prc.waitFor();
    }

    public static Attack[] loadAttacks() throws Exception {
        File attacksFile = new File("attacks.config");
        ArrayList<String> expressions = new ArrayList<String>();
        if (!attacksFile.exists()) {
            expressions.add("");
            String[] exps = new String[expressions.size()];
            for (int i = 0; i < exps.length; i++) {
                exps[i] = expressions.get(i);
            }
            Attack.saveAttacks(exps);
        } else {
            Scanner aConfigFileScanner = new Scanner(attacksFile);
            aConfigFileScanner = aConfigFileScanner.useDelimiter(";");
            while (aConfigFileScanner.hasNext()) {
                String exp = aConfigFileScanner.next().trim();
                if (exp.length() > 0) {
                    expressions.add(exp);
                }
            }
        }
        Attack[] attacks = new Attack[expressions.size()];
        for (int i = 0; i < attacks.length; i++) {
            attacks[i] = new Attack(expressions.get(i));
        }
        return attacks;
    }

    public static void saveAttacks(String[] attacks) throws Exception {
        File attacksFile = new File("attacks.config");
        if (attacksFile.exists()) {
            try {
                attacksFile.delete();
            } catch (Exception ex) {
                throw new Exception("Unable to overwrite 'attacks.config'.");
            }
        }
        try {
            if (attacksFile.createNewFile()) {
                FileWriter cConfigFileWriter = new FileWriter(attacksFile);
                for (String exp : attacks) {
                    cConfigFileWriter.write(exp + ";\r\n");
                }
                cConfigFileWriter.close();
            }
        } catch (IOException ex) {
            throw new Exception("Unable to write 'attacks.config'.");
        }
    }
}
