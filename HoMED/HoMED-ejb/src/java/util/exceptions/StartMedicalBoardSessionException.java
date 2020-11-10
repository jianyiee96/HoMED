/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class StartMedicalBoardSessionException extends Exception {

    /**
     * Creates a new instance of <code>StartMedicalBoardSessionException</code>
     * without detail message.
     */
    public StartMedicalBoardSessionException() {
    }

    /**
     * Constructs an instance of <code>StartMedicalBoardSessionException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public StartMedicalBoardSessionException(String msg) {
        super(msg);
    }
}
