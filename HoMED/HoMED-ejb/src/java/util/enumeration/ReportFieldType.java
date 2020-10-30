package util.enumeration;

public enum ReportFieldType {
    HEADER("Header"),
    LINE("Line Chart"),
    BAR("Bar Chart"),
    PIE("Pie Chart");

    private String text;

    private ReportFieldType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    public static ReportFieldType[] getReportFieldTypes(ReportDataGrouping reportDataGrouping) {
        if (reportDataGrouping == ReportDataGrouping.S_PES
                || reportDataGrouping == ReportDataGrouping.S_BT) {
            return new ReportFieldType[]{BAR, PIE};
        }
        return values();
    }
}
