/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanEmailExistException extends Exception {

    /**
     * Creates a new instance of <code>ServicemanEmailExistException</code>
     * without detail message.
     */
    public ServicemanEmailExistException() {
    }

    /**
     * Constructs an instance of <code>ServicemanEmailExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServicemanEmailExistException(String msg) {
        super(msg);
    }
}
