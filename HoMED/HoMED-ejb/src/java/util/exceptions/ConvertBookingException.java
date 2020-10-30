/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class ConvertBookingException extends Exception {

    /**
     * Creates a new instance of <code>ConvertBookingException</code> without
     * detail message.
     */
    public ConvertBookingException() {
    }

    /**
     * Constructs an instance of <code>ConvertBookingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ConvertBookingException(String msg) {
        super(msg);
    }
}
