/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author sunag
 */
public class DuplicateEntryExistsException extends Exception {

    /**
     * Creates a new instance of <code>DuplicateEntryExistsException</code>
     * without detail message.
     */
    public DuplicateEntryExistsException() {
    }

    /**
     * Constructs an instance of <code>DuplicateEntryExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DuplicateEntryExistsException(String msg) {
        super(msg);
    }
}
