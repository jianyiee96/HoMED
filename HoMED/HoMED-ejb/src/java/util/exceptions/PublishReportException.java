/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class PublishReportException extends Exception {

    /**
     * Creates a new instance of <code>PublishReportException</code> without
     * detail message.
     */
    public PublishReportException() {
    }

    /**
     * Constructs an instance of <code>PublishReportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PublishReportException(String msg) {
        super(msg);
    }
}
