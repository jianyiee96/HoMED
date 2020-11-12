/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class ExpireSlotException extends Exception {

    /**
     * Creates a new instance of <code>ExpireSlotException</code> without detail
     * message.
     */
    public ExpireSlotException() {
    }

    /**
     * Constructs an instance of <code>ExpireSlotException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExpireSlotException(String msg) {
        super(msg);
    }
}
