/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

/**
 *
 * @author User
 */
public enum PesStatusEnum {
    A("Suitable for frontline operational vocations"),
    B1("Suitable for frontline operational vocations"),
    B2("Suitable for some frontline operational vocations, and frontline support vocations"),
    B3("Suitable for some frontline operational vocations, and frontline support vocations"),
    B4("Suitable for some frontline operational vocations, and frontline support vocations"),
    BP("Fit for Obese BRT"),
    C2("Suitable for some frontline support vocations"),
    C9("Suitable for some frontline support vocations"),
    E1("Suitable for administrative support vocations"),
    E9("Suitable for administrative support vocations"),
    F("Medically unfit for any form of service");

    private String stringVal;

    private PesStatusEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }
}
