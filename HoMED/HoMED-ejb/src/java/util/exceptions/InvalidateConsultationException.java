/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class InvalidateConsultationException extends Exception {

    /**
     * Creates a new instance of <code>InvalidateConsultationException</code>
     * without detail message.
     */
    public InvalidateConsultationException() {
    }

    /**
     * Constructs an instance of <code>InvalidateConsultationException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidateConsultationException(String msg) {
        super(msg);
    }
}
