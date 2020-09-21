/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class PasswordsDoNotMatchException extends Exception {

    /**
     * Creates a new instance of <code>PasswordsDoNotMatchException</code>
     * without detail message.
     */
    public PasswordsDoNotMatchException() {
    }

    /**
     * Constructs an instance of <code>PasswordsDoNotMatchException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public PasswordsDoNotMatchException(String msg) {
        super(msg);
    }
}
