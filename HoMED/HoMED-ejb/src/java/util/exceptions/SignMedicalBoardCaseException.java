/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class SignMedicalBoardCaseException extends Exception {

    /**
     * Creates a new instance of <code>SignMedicalBoardCaseException</code>
     * without detail message.
     */
    public SignMedicalBoardCaseException() {
    }

    /**
     * Constructs an instance of <code>SignMedicalBoardCaseException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public SignMedicalBoardCaseException(String msg) {
        super(msg);
    }
}
