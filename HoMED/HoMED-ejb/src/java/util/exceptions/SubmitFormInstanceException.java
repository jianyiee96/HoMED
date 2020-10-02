/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class SubmitFormInstanceException extends Exception {

    /**
     * Creates a new instance of <code>SubmitFormInstanceException</code>
     * without detail message.
     */
    public SubmitFormInstanceException() {
    }

    /**
     * Constructs an instance of <code>SubmitFormInstanceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SubmitFormInstanceException(String msg) {
        super(msg);
    }
}
