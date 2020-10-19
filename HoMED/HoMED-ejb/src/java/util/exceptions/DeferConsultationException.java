/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Jin
 */
public class DeferConsultationException extends Exception {

    /**
     * Creates a new instance of <code>DeferConsultationException</code> without
     * detail message.
     */
    public DeferConsultationException() {
    }

    /**
     * Constructs an instance of <code>DeferConsultationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeferConsultationException(String msg) {
        super(msg);
    }
}
