/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class CreateEmployeeException extends Exception {

    /**
     * Creates a new instance of <code>CreateEmployeeException</code> without
     * detail message.
     */
    public CreateEmployeeException() {
    }

    /**
     * Constructs an instance of <code>CreateEmployeeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateEmployeeException(String msg) {
        super(msg);
    }
}
