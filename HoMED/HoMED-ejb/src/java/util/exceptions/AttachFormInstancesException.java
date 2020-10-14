/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class AttachFormInstancesException extends Exception {

    /**
     * Creates a new instance of <code>AttachFormInstancesException</code>
     * without detail message.
     */
    public AttachFormInstancesException() {
    }

    /**
     * Constructs an instance of <code>AttachFormInstancesException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public AttachFormInstancesException(String msg) {
        super(msg);
    }
}
