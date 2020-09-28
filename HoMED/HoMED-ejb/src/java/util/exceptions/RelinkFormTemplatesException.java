/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class RelinkFormTemplatesException extends Exception {

    /**
     * Creates a new instance of <code>RelinkFormTemplatesException</code>
     * without detail message.
     */
    public RelinkFormTemplatesException() {
    }

    /**
     * Constructs an instance of <code>RelinkFormTemplatesException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RelinkFormTemplatesException(String msg) {
        super(msg);
    }
}
