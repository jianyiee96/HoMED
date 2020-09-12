/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ServicemanNotFoundException</code>
     * without detail message.
     */
    public ServicemanNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ServicemanNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServicemanNotFoundException(String msg) {
        super(msg);
    }
}
