/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

/**
 *
 * @author Bryan
 */
public class ReportNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ReportNotFoundException</code> without
     * detail message.
     */
    public ReportNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ReportNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReportNotFoundException(String msg) {
        super(msg);
    }
}
