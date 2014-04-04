package ph.edu.dlsu.chimera.core;

/**
 * An instance of this class constitutes an object which allows values to be
 * returned through function parameters. Useful for when returning multiple
 * values of different types.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ReturnParameter {

    private Object returnedObject;

    /**
     * Sets the object to be returned
     *
     * @param returnedObject The object to be returned
     */
    public void setReturnedObject(Object returnedObject) {
        this.returnedObject = returnedObject;
    }

    /**
     *
     * @return The returned object
     */
    public Object getReturnedObject() {
        return returnedObject;
    }

}
