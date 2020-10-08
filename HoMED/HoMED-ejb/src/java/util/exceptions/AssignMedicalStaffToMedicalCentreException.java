/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

/**
 *
 * @author Bryan
 */
public class AssignMedicalStaffToMedicalCentreException extends Exception {

    /**
     * Creates a new instance of
     * <code>AssignMedicalStaffToMedicalCentreException</code> without detail
     * message.
     */
    public AssignMedicalStaffToMedicalCentreException() {
    }

    /**
     * Constructs an instance of
     * <code>AssignMedicalStaffToMedicalCentreException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AssignMedicalStaffToMedicalCentreException(String msg) {
        super(msg);
    }
}
