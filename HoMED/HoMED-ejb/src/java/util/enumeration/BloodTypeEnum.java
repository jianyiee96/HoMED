/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public enum BloodTypeEnum {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private String bloodTypeString;

    private BloodTypeEnum(String bloodTypeString) {
        this.bloodTypeString = bloodTypeString;
    }

    public String getBloodTypeString() {
        return bloodTypeString;
    }
}
