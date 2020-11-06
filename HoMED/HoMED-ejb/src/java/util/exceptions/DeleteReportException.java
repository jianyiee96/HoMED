/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class DeleteReportException extends Exception {

    /**
     * Creates a new instance of <code>DeleteReportException</code> without
     * detail message.
     */
    public DeleteReportException() {
    }

    /**
     * Constructs an instance of <code>DeleteReportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteReportException(String msg) {
        super(msg);
    }
}
