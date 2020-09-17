/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class UpdateServicemanException extends Exception {

    /**
     * Creates a new instance of <code>UpdateServicemanException</code> without
     * detail message.
     */
    public UpdateServicemanException() {
    }

    /**
     * Constructs an instance of <code>UpdateServicemanException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateServicemanException(String msg) {
        super(msg);
    }
}
