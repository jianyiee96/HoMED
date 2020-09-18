/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class ResetEmployeePasswordException extends Exception {

    /**
     * Creates a new instance of <code>ResetEmployeePasswordException</code>
     * without detail message.
     */
    public ResetEmployeePasswordException() {
    }

    /**
     * Constructs an instance of <code>ResetEmployeePasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ResetEmployeePasswordException(String msg) {
        super(msg);
    }
}
