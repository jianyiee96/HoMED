/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class MarkBookingAbsentException extends Exception {

    /**
     * Creates a new instance of <code>MarkBookingAbsentException</code> without
     * detail message.
     */
    public MarkBookingAbsentException() {
    }

    /**
     * Constructs an instance of <code>MarkBookingAbsentException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MarkBookingAbsentException(String msg) {
        super(msg);
    }
}
