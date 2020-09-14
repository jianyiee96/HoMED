/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanInvalidPasswordException extends Exception {

    /**
     * Creates a new instance of <code>ServicemanInvalidPasswordException</code>
     * without detail message.
     */
    public ServicemanInvalidPasswordException() {
    }

    /**
     * Constructs an instance of <code>ServicemanInvalidPasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServicemanInvalidPasswordException(String msg) {
        super(msg);
    }
}
