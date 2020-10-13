/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class CreateConsultationException extends Exception {

    /**
     * Creates a new instance of <code>CreateConsultationException</code>
     * without detail message.
     */
    public CreateConsultationException() {
    }

    /**
     * Constructs an instance of <code>CreateConsultationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateConsultationException(String msg) {
        super(msg);
    }
}
