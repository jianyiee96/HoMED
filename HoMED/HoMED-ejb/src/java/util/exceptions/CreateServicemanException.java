/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class CreateServicemanException extends Exception {

    /**
     * Creates a new instance of <code>CreateServicemanException</code> without
     * detail message.
     */
    public CreateServicemanException() {
    }

    /**
     * Constructs an instance of <code>CreateServicemanException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateServicemanException(String msg) {
        super(msg);
    }
}
