/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class StartConsultationException extends Exception {

    /**
     * Creates a new instance of <code>StartConsultationException</code> without
     * detail message.
     */
    public StartConsultationException() {
    }

    /**
     * Constructs an instance of <code>StartConsultationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public StartConsultationException(String msg) {
        super(msg);
    }
}
