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
                || reportDataGrouping == ReportDataGrouping.S_BT
                || reportDataGrouping == ReportDataGrouping.S_BK
                || reportDataGrouping == ReportDataGrouping.MO_CS
                || reportDataGrouping == ReportDataGrouping.MO_FI
                || reportDataGrouping == ReportDataGrouping.MO_MC) {
            return new ReportFieldType[]{BAR, PIE};
        } else if (reportDataGrouping == ReportDataGrouping.C_QT_MC
                || reportDataGrouping == ReportDataGrouping.C_QT_CP) {
            return new ReportFieldType[]{LINE};
        }
        return new ReportFieldType[]{};
    }
}
