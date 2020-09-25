/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class UpdateFormInstanceException extends Exception {

    /**
     * Creates a new instance of <code>UpdateFormInstanceException</code>
     * without detail message.
     */
    public UpdateFormInstanceException() {
    }

    /**
     * Constructs an instance of <code>UpdateFormInstanceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateFormInstanceException(String msg) {
        super(msg);
    }
}
