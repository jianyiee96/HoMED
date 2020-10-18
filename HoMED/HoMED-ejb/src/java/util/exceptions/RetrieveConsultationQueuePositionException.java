/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class RetrieveConsultationQueuePositionException extends Exception {

    /**
     * Creates a new instance of
     * <code>RetrieveConsultationQueuePositionException</code> without detail
     * message.
     */
    public RetrieveConsultationQueuePositionException() {
    }

    /**
     * Constructs an instance of
     * <code>RetrieveConsultationQueuePositionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RetrieveConsultationQueuePositionException(String msg) {
        super(msg);
    }
}
