package util.enumeration;

public enum ReportDataGrouping {
    S_BT("Blood Type", "Blood Type", "Number of Serviceman"),
    S_BK("Bookings", "Number of Bookings", "Number of Serviceman"),
    S_PES("PES Status", "Pes Status", "Number of Serviceman"),
    MO_CS("Consultations", "Number of Consultations", "Number of Medical Officers"),
    MO_FI("Forms Signed", "Number of Forms Signed", "Number of Medical Officers"),
    MO_MC("Medical Centre", "Medical Centre", "Number of Medical Officers"),
    C_Q_MC("Medical Centre", "", ""),
    C_Q_CP("Consultaiton Purpose", "", ""),
    C_W_MC("Medical Centre", "", ""),
    C_W_HR("Hour Of Day", "", ""),
    C_D_MC("Medical Centre", "", ""),
    C_D_CP("Consultation Purpose", "", "");

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
            if (value == ReportDataValue.QTY) {
                return new ReportDataGrouping[]{S_BT, S_BK, S_PES};
            }
        } else if (type == ReportDataType.MEDICAL_OFFICER) {
            if (value == ReportDataValue.QTY) {
                return new ReportDataGrouping[]{MO_CS, MO_FI, MO_MC};
            }
        } else if (type == ReportDataType.CONSULTATION) {
            if (value == ReportDataValue.QTY) {
                return new ReportDataGrouping[]{C_Q_MC, C_Q_CP};
            } else if (value == ReportDataValue.QWT) {
                return new ReportDataGrouping[]{C_W_MC, C_W_HR};
            } else if (value == ReportDataValue.DC) {
                return new ReportDataGrouping[]{C_D_MC, C_D_CP};
            }
        }
        return new ReportDataGrouping[]{};
    }
}
