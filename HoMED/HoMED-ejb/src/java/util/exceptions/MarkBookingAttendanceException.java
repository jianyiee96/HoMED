/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class MarkBookingAttendanceException extends Exception {

    /**
     * Creates a new instance of <code>MarkBookingAttendanceException</code>
     * without detail message.
     */
    public MarkBookingAttendanceException() {
    }

    /**
     * Constructs an instance of <code>MarkBookingAttendanceException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MarkBookingAttendanceException(String msg) {
        super(msg);
    }
}
