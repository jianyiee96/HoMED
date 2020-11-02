package util.enumeration;

public enum ReportDataType {
    SERVICEMAN("Serviceman"),
    MEDICAL_OFFICER("Medical Officer"),
    CONSULTATION("Consultation"),
    MEDICAL_BOARD("Medical Board"),
    FORM("Form");

    private String text;

    private ReportDataType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
