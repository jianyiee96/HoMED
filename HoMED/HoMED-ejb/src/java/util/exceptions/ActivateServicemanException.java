/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class ActivateServicemanException extends Exception {

    /**
     * Creates a new instance of <code>ActivateServicemanException</code>
     * without detail message.
     */
    public ActivateServicemanException() {
    }

    /**
     * Constructs an instance of <code>ActivateServicemanException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ActivateServicemanException(String msg) {
        super(msg);
    }
}
