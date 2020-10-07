package util.enumeration;

public enum ServicemanRoleEnum {

    REGULAR("HT Regular"),
    NSF("NSF"),
    NSMEN("NSmen"),
    OTHERS("Volunteers/Civilian");

    private String stringVal;

    private ServicemanRoleEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }
}
