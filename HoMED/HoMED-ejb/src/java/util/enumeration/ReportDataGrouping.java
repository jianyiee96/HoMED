package util.enumeration;

public enum ReportDataGrouping {
    S_PES("PES Status"),
    S_BT("Blood Type"),
    S_BK("Bookings"),
    MO_CS("Consultations"),
    MO_FI("Forms Signed"),
    MO_MC("Medical Centre");

    private String text;

    private ReportDataGrouping(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ReportDataGrouping[] getReportDataGroupings(ReportDataType type, ReportDataValue value) {
        if (type == ReportDataType.SERVICEMAN) {
            return new ReportDataGrouping[]{S_PES, S_BT, S_BK};
        } else if (type == ReportDataType.MEDICAL_OFFICER) {
            if (value == ReportDataValue.QTY) {
                return new ReportDataGrouping[]{MO_CS, MO_FI, MO_MC};
            }
        }
        return new ReportDataGrouping[]{};
    }
}
