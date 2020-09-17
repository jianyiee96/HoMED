/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class DeleteEmployeeException extends Exception {

    /**
     * Creates a new instance of <code>DeleteEmployeeException</code> without
     * detail message.
     */
    public DeleteEmployeeException() {
    }

    /**
     * Constructs an instance of <code>DeleteEmployeeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteEmployeeException(String msg) {
        super(msg);
    }
}
