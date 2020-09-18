/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.exceptions;

public class MedicalCentreNotFoundException extends Exception {

    public MedicalCentreNotFoundException() {
    }

    public MedicalCentreNotFoundException(String msg) {
        super(msg);
    }
}
