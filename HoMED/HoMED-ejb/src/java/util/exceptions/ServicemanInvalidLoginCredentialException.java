/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanInvalidLoginCredentialException extends Exception {

    /**
     * Creates a new instance of
     * <code>ServicemanInvalidLoginCredentialException</code> without detail
     * message.
     */
    public ServicemanInvalidLoginCredentialException() {
    }

    /**
     * Constructs an instance of
     * <code>ServicemanInvalidLoginCredentialException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ServicemanInvalidLoginCredentialException(String msg) {
        super(msg);
    }
}
