/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class ChangeEmployeePasswordException extends Exception {

    /**
     * Creates a new instance of <code>ChangeEmployeePasswordException</code>
     * without detail message.
     */
    public ChangeEmployeePasswordException() {
    }

    /**
     * Constructs an instance of <code>ChangeEmployeePasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ChangeEmployeePasswordException(String msg) {
        super(msg);
    }
}
