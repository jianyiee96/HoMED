/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class ArchiveFormInstanceException extends Exception {

    /**
     * Creates a new instance of <code>ArchiveFormInstanceException</code>
     * without detail message.
     */
    public ArchiveFormInstanceException() {
    }

    /**
     * Constructs an instance of <code>ArchiveFormInstanceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ArchiveFormInstanceException(String msg) {
        super(msg);
    }
}
