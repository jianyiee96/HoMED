/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class UpdateEmployeeException extends Exception {

    /**
     * Creates a new instance of <code>UpdateEmployeeException</code> without
     * detail message.
     */
    public UpdateEmployeeException() {
    }

    /**
     * Constructs an instance of <code>UpdateEmployeeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateEmployeeException(String msg) {
        super(msg);
    }
}
