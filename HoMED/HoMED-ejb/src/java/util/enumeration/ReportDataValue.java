package util.enumeration;

public enum ReportDataValue {
    QTY("Quantity"),
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
        }
        return new ReportDataValue[]{QTY};
    }
}
