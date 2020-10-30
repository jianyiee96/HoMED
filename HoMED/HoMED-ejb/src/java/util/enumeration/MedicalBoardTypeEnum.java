/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum MedicalBoardTypeEnum {
    ABSENCE("Medical Board In Presence"),
    PRESENCE("Medical Board In Absence");

    private String stringVal;

    private MedicalBoardTypeEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }

}
