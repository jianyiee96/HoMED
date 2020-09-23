/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author tanwk
 */
public class DeleteServicemanException extends Exception {

    /**
     * Creates a new instance of <code>DeleteServicemanException</code> without
     * detail message.
     */
    public DeleteServicemanException() {
    }

    /**
     * Constructs an instance of <code>DeleteServicemanException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteServicemanException(String msg) {
        super(msg);
    }
}
