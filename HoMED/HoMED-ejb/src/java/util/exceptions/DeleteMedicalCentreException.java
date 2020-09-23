/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class DeleteMedicalCentreException extends Exception {

    /**
     * Creates a new instance of <code>DeleteMedicalCentreException</code>
     * without detail message.
     */
    public DeleteMedicalCentreException() {
    }

    /**
     * Constructs an instance of <code>DeleteMedicalCentreException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteMedicalCentreException(String msg) {
        super(msg);
    }
}
