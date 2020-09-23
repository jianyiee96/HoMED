/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class ChangeServicemanPasswordException extends Exception {

    /**
     * Creates a new instance of <code>ChangeServicemanPasswordException</code>
     * without detail message.
     */
    public ChangeServicemanPasswordException() {
    }

    /**
     * Constructs an instance of <code>ChangeServicemanPasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ChangeServicemanPasswordException(String msg) {
        super(msg);
    }
}
