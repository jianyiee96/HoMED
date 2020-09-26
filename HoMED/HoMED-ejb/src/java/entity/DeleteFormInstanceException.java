/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

/**
 *
 * @author User
 */
public class DeleteFormInstanceException extends Exception {

    /**
     * Creates a new instance of <code>DeleteFormInstanceException</code>
     * without detail message.
     */
    public DeleteFormInstanceException() {
    }

    /**
     * Constructs an instance of <code>DeleteFormInstanceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteFormInstanceException(String msg) {
        super(msg);
    }
}
