/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PolicyChain {

    private int position;
    private String[] subchains;

    public PolicyChain(int position, int size) {
        this.position = position;
        this.subchains = new String[size];
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSubChain(int i, String subchainName) {
        this.subchains[i] = subchainName;
    }

    public String getSubChain(int i) {
        return this.subchains[i];
    }

    public int size() {
        return this.subchains.length;
    }
}
