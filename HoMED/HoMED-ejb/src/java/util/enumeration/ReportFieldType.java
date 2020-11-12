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
                || reportDataGrouping == ReportDataGrouping.S_ROLE
                || reportDataGrouping == ReportDataGrouping.S_PES
                || reportDataGrouping == ReportDataGrouping.MO_CS
                || reportDataGrouping == ReportDataGrouping.MO_FI
                || reportDataGrouping == ReportDataGrouping.MO_MC
                || reportDataGrouping == ReportDataGrouping.C_Q_MC
                || reportDataGrouping == ReportDataGrouping.C_Q_CP
                || reportDataGrouping == ReportDataGrouping.C_W_MC
                || reportDataGrouping == ReportDataGrouping.C_D_MC
                || reportDataGrouping == ReportDataGrouping.C_D_CP
                || reportDataGrouping == ReportDataGrouping.MB_Q_C
                || reportDataGrouping == ReportDataGrouping.MB_Q_T) {
            return new ReportFieldType[]{BAR, PIE};
        } else if (reportDataGrouping == ReportDataGrouping.C_QT_MC
                || reportDataGrouping == ReportDataGrouping.C_QT_CP
                || reportDataGrouping == ReportDataGrouping.C_W_HR
                || reportDataGrouping == ReportDataGrouping.MB_QT_T) {
            return new ReportFieldType[]{LINE};
        }
        return new ReportFieldType[]{};
    }
}
