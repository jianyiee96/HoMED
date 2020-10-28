package util.enumeration;

public enum ReportDataType {
    SERVICEMAN("Serivceman"),
    MEDICAL_OFFICER("Medical Officer"),
    MEDICAL_BOARD("Medical Board"),
    CONSULTATION("Consultation");

    private String text;

    private ReportDataType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
