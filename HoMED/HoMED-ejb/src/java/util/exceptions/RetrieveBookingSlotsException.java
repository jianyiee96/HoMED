/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class RetrieveBookingSlotsException extends Exception {

    /**
     * Creates a new instance of <code>RetrieveBookingSlotsException</code>
     * without detail message.
     */
    public RetrieveBookingSlotsException() {
    }

    /**
     * Constructs an instance of <code>RetrieveBookingSlotsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RetrieveBookingSlotsException(String msg) {
        super(msg);
    }
}
