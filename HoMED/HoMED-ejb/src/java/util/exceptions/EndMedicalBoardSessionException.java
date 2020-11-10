/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class EndMedicalBoardSessionException extends Exception {

    /**
     * Creates a new instance of <code>EndMedicalBoardSessionException</code>
     * without detail message.
     */
    public EndMedicalBoardSessionException() {
    }

    /**
     * Constructs an instance of <code>EndMedicalBoardSessionException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EndMedicalBoardSessionException(String msg) {
        super(msg);
    }
}
