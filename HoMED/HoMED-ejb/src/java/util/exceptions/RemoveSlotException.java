/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class RemoveSlotException extends Exception {

    /**
     * Creates a new instance of <code>RemoveSlotException</code> without detail
     * message.
     */
    public RemoveSlotException() {
    }

    /**
     * Constructs an instance of <code>RemoveSlotException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RemoveSlotException(String msg) {
        super(msg);
    }
}
