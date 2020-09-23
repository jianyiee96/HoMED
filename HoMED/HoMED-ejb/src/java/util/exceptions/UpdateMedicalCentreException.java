/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class UpdateMedicalCentreException extends Exception {

    /**
     * Creates a new instance of <code>UpdateMedicalCentreException</code>
     * without detail message.
     */
    public UpdateMedicalCentreException() {
    }

    /**
     * Constructs an instance of <code>UpdateMedicalCentreException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateMedicalCentreException(String msg) {
        super(msg);
    }
}
