/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class EndConsultationException extends Exception {

    /**
     * Creates a new instance of <code>EndConsultationException</code> without
     * detail message.
     */
    public EndConsultationException() {
    }

    /**
     * Constructs an instance of <code>EndConsultationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public EndConsultationException(String msg) {
        super(msg);
    }
}
