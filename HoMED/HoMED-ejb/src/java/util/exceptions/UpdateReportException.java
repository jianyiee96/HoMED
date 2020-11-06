/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class UpdateReportException extends Exception {

    /**
     * Creates a new instance of <code>UpdateReportException</code> without
     * detail message.
     */
    public UpdateReportException() {
    }

    /**
     * Constructs an instance of <code>UpdateReportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateReportException(String msg) {
        super(msg);
    }
}
