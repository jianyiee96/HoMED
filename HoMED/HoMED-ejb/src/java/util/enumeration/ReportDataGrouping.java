package util.enumeration;

public enum ReportDataGrouping {
    S_PES("PES Status"),
    S_BT("Blood Type"),
    S_BK("Bookings");

    private String text;

    private ReportDataGrouping(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ReportDataGrouping[] getReportDataGroupings(ReportDataType type) {
        if (type == ReportDataType.SERVICEMAN) {
            return new ReportDataGrouping[]{S_PES, S_BT, S_BK};
        }
        return new ReportDataGrouping[]{};
    }
}
