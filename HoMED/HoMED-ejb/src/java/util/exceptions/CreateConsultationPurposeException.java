/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class CreateConsultationPurposeException extends Exception {

    /**
     * Creates a new instance of <code>CreateConsultationPurposeException</code>
     * without detail message.
     */
    public CreateConsultationPurposeException() {
    }

    /**
     * Constructs an instance of <code>CreateConsultationPurposeException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateConsultationPurposeException(String msg) {
        super(msg);
    }
}
