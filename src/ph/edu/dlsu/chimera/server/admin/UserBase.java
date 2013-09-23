package ph.edu.dlsu.chimera.server.admin;

/**
 * An instance of this class constitutes a module responsible for managing user credentials.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class UserBase {

    /**
     * Constructs a new UserBase object.
     */
    public UserBase() {
    }

    /**
     * Check if the supplied credentials exists in the userbase.
     * @param uname - username
     * @param passhash - password MD5 hash
     * @return whether or not the specified credentials were found.
     */
    public boolean lookup(String uname, String passhash) {
        return true;
    }

}
