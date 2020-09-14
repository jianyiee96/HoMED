/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class EmployeeInvalidLoginCredentialException extends Exception {

    /**
     * Creates a new instance of
     * <code>EmployeeInvalidLoginCredentialException</code> without detail
     * message.
     */
    public EmployeeInvalidLoginCredentialException() {
    }

    /**
     * Constructs an instance of
     * <code>EmployeeInvalidLoginCredentialException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeInvalidLoginCredentialException(String msg) {
        super(msg);
    }
}
