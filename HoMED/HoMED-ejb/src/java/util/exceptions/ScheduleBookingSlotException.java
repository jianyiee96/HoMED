/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

public class ScheduleBookingSlotException extends Exception {

    /**
     * Creates a new instance of <code>ScheduleBookingSlotException</code>
     * without detail message.
     */
    public ScheduleBookingSlotException() {
    }

    /**
     * Constructs an instance of <code>ScheduleBookingSlotException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ScheduleBookingSlotException(String msg) {
        super(msg);
    }
}
