package util.enumeration;

public enum ReportFieldType {
    HEADER("Header"),
    LINE("Line Chart"),
    BAR("Bar Chart"),
    PIE("Pie Chart"),
    DATASET("Data Set");

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
                || reportDataGrouping == ReportDataGrouping.MO_MC
                || reportDataGrouping == ReportDataGrouping.C_Q_MC
                || reportDataGrouping == ReportDataGrouping.C_Q_CP
                || reportDataGrouping == ReportDataGrouping.C_W_MC
                || reportDataGrouping == ReportDataGrouping.C_D_MC
                || reportDataGrouping == ReportDataGrouping.C_D_CP) {
            return new ReportFieldType[]{BAR, PIE};
        } else if (reportDataGrouping == ReportDataGrouping.C_QT_MC
                || reportDataGrouping == ReportDataGrouping.C_QT_CP
                || reportDataGrouping == ReportDataGrouping.C_W_HR) {
            return new ReportFieldType[]{LINE};
        }
        return new ReportFieldType[]{};
    }
}
