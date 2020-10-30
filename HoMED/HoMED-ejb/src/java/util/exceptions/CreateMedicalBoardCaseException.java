/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class CreateMedicalBoardCaseException extends Exception {

    /**
     * Creates a new instance of <code>CreateMedicalBoardCaseException</code>
     * without detail message.
     */
    public CreateMedicalBoardCaseException() {
    }

    /**
     * Constructs an instance of <code>CreateMedicalBoardCaseException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateMedicalBoardCaseException(String msg) {
        super(msg);
    }
}
