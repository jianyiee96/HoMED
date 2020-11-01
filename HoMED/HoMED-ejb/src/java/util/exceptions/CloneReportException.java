/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class CloneReportException extends Exception {

    /**
     * Creates a new instance of <code>CloneReportException</code> without
     * detail message.
     */
    public CloneReportException() {
    }

    /**
     * Constructs an instance of <code>CloneReportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CloneReportException(String msg) {
        super(msg);
    }
}
