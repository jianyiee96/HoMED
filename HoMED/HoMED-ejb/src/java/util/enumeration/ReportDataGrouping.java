package util.enumeration;

public enum ReportDataGrouping {
    S_PES("PES Status", "Pes Status", "Number of Serviceman"),
    S_BT("Blood Type", "Blood Type", "Number of Serviceman"),
    S_BK("Bookings", "Number of Bookings", "Number of Serviceman"),
    MO_CS("Consultations", "Number of Consultations", "Number of Medical Officers"),
    MO_FI("Forms Signed", "Number of Forms Signed", "Number of Medical Officers"),
    MO_MC("Medical Centre", "Medical Centre", "Number of Medical Officers");

    private String text;
    private String x_axis;
    private String y_axis;

    private ReportDataGrouping(String text, String x_axis, String y_axis) {
        this.text = text;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
    }

    public String getText() {
        return text;
    }

    public String getX_axis() {
        return x_axis;
    }

    public String getY_axis() {
        return y_axis;
    }

    public Boolean requireDate() {
        return this == S_BK
                || this == MO_CS
                || this == MO_FI;
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
