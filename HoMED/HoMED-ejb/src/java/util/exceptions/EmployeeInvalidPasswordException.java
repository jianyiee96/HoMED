/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class EmployeeInvalidPasswordException extends Exception {

    /**
     * Creates a new instance of <code>EmployeeInvalidPasswordException</code>
     * without detail message.
     */
    public EmployeeInvalidPasswordException() {
    }

    /**
     * Constructs an instance of <code>EmployeeInvalidPasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeInvalidPasswordException(String msg) {
        super(msg);
    }
}
