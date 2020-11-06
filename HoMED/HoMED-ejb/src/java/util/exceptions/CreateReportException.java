/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class CreateReportException extends Exception {

    /**
     * Creates a new instance of <code>CreateReportException</code> without
     * detail message.
     */
    public CreateReportException() {
    }

    /**
     * Constructs an instance of <code>CreateReportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateReportException(String msg) {
        super(msg);
    }
}
