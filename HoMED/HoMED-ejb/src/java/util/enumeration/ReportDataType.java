package util.enumeration;

public enum ReportDataType {
    SERVICEMAN("Serviceman"),
    MEDICAL_OFFICER("Medical Officer"),
    MEDICAL_BOARD("Medical Board"),
    CONSULTATION("Consultation"),
    FORM("Form");

    private String text;

    private ReportDataType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
