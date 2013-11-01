/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Config {

    public int adminPort;
    public String ifExternal;
    public String ifInternal;

    public Config() {
        this.adminPort = -1;
        this.ifExternal = null;
        this.ifInternal = null;
    }
}
