/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class ActivateEmployeeException extends Exception {

    /**
     * Creates a new instance of <code>ActivateEmployeeException</code> without
     * detail message.
     */
    public ActivateEmployeeException() {
    }

    /**
     * Constructs an instance of <code>ActivateEmployeeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ActivateEmployeeException(String msg) {
        super(msg);
    }
}
