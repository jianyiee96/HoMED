package util.enumeration;

public enum ReportDataValue {
    QTY("Quantity"),
    QWT("Queue Waiting Time"),
    DC("Duration Of Consultation"),
    TREND("Trend");

    private String text;

    private ReportDataValue(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ReportDataValue[] getReportDataTypeValues(ReportDataType type) {
        if (type == ReportDataType.SERVICEMAN) {
            return new ReportDataValue[]{QTY};
        } else if (type == ReportDataType.MEDICAL_OFFICER) {
            return new ReportDataValue[]{QTY};
        } else if (type == ReportDataType.CONSULTATION) {
            return new ReportDataValue[]{QTY, QWT, DC};
        }
        return new ReportDataValue[]{};
    }
}
