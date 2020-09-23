/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class CreateMedicalCentreException extends Exception {

    /**
     * Creates a new instance of <code>CreateMedicalCentreException</code>
     * without detail message.
     */
    public CreateMedicalCentreException() {
    }

    /**
     * Constructs an instance of <code>CreateMedicalCentreException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateMedicalCentreException(String msg) {
        super(msg);
    }
}
