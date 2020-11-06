/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum MedicalBoardTypeEnum {
    ABSENCE("Medical Board In Absence"),
    PRESENCE("Medical Board In Presence");

    private String stringVal;

    private MedicalBoardTypeEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }

}
