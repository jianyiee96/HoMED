/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class CreateNotificationException extends Exception {

    /**
     * Creates a new instance of <code>CreateNotificationException</code>
     * without detail message.
     */
    public CreateNotificationException() {
    }

    /**
     * Constructs an instance of <code>CreateNotificationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNotificationException(String msg) {
        super(msg);
    }
}
