/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class UpdateBookingCommentException extends Exception {

    /**
     * Creates a new instance of <code>UpdateBookingCommentException</code>
     * without detail message.
     */
    public UpdateBookingCommentException() {
    }

    /**
     * Constructs an instance of <code>UpdateBookingCommentException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateBookingCommentException(String msg) {
        super(msg);
    }
}
