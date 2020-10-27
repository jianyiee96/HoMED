/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum FormFieldAccessEnum {
    SERVICEMAN("Serviceman Only"),
    MO("Medical Officer Only"),
    SERVICEMAN_MO("Serviceman and Medical Officer Only"),
    BOARD("Medical Board Only");

    private String text;

    private FormFieldAccessEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
