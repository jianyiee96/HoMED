/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class ResetServicemanPasswordException extends Exception {

    /**
     * Creates a new instance of <code>ResetServicemanPasswordException</code>
     * without detail message.
     */
    public ResetServicemanPasswordException() {
    }

    /**
     * Constructs an instance of <code>ResetServicemanPasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ResetServicemanPasswordException(String msg) {
        super(msg);
    }
}
