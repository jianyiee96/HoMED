/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class CreateBookingException extends Exception {

    /**
     * Creates a new instance of <code>CreateBookingException</code> without
     * detail message.
     */
    public CreateBookingException() {
    }

    /**
     * Constructs an instance of <code>CreateBookingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateBookingException(String msg) {
        super(msg);
    }
}
