/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanNricExistException extends Exception {

    /**
     * Creates a new instance of <code>ServicemanNricExistException</code>
     * without detail message.
     */
    public ServicemanNricExistException() {
    }

    /**
     * Constructs an instance of <code>ServicemanNricExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServicemanNricExistException(String msg) {
        super(msg);
    }
}
