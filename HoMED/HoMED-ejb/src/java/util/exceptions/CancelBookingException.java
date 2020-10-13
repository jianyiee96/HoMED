/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class CancelBookingException extends Exception {

    /**
     * Creates a new instance of <code>CancelBookingException</code> without
     * detail message.
     */
    public CancelBookingException() {
    }

    /**
     * Constructs an instance of <code>CancelBookingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CancelBookingException(String msg) {
        super(msg);
    }
}
