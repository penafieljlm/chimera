/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

/**
 *
 * @author AMD
 */
public class cmtest {

    public static void main(String[] args) throws InterruptedException {
        String chars = "|/-\\";
        int idx = 0;
        for (int i = 0; i < 100; i++) {
            System.out.print("\b \b" + chars.charAt(idx));
            Thread.sleep(50);
            idx = ++idx % chars.length();
        }
        System.out.print("\b \bX");
    }
}
