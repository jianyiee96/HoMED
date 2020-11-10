package util.enumeration;

public enum ReportDataGrouping {
    S_BT("Blood Type"),
    S_BK("Bookings"),
    S_PES("PES Status"),
    S_ROLE("Role"),
    MO_CS("Consultations"),
    MO_FI("Forms Signed"),
    MO_MC("Medical Centre"),
    C_Q_MC("Medical Centre"),
    C_Q_CP("Consultation Purpose"),
    C_W_MC("Medical Centre"),
    C_W_HR("Hour Of Day"),
    C_D_MC("Medical Centre"),
    C_D_CP("Consultation Purpose"),
    C_QT_MC("Medical Centre"),
    C_QT_CP("Consultation Purpose"),
    MB_Q_C("Diagnosed Conditions"),
    MB_Q_T("Medical Board Type"),
    MB_QT_T("Medical Board Type");

    private String text;

    private ReportDataGrouping(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Boolean requireDate() {
        return this == S_BK
                || this == MO_CS
                || this == MO_FI
                || this == C_Q_MC
                || this == C_Q_CP
                || this == C_QT_MC
                || this == C_QT_CP
                || this == C_W_MC
                || this == C_W_HR
                || this == C_D_MC
                || this == C_D_CP
                || this == MB_Q_C
                || this == MB_Q_T
                || this == MB_QT_T;
    }

    public static ReportDataGrouping[] getReportDataGroupings(ReportDataType type, ReportDataValue value) {
        if (type == ReportDataType.SERVICEMAN) {
            if (value == ReportDataValue.QTY) {
                return new ReportDataGrouping[]{S_BT, S_BK, S_ROLE, S_PES};
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
            } else if (value == ReportDataValue.TREND) {
                return new ReportDataGrouping[]{C_QT_MC, C_QT_CP};
            }
        } else if (type == ReportDataType.MEDICAL_BOARD) {
            if (value == ReportDataValue.QTY) {
                return new ReportDataGrouping[]{MB_Q_C, MB_Q_T};
            } else if (value == ReportDataValue.TREND) {
                return new ReportDataGrouping[]{MB_QT_T};
            }
        }
        return new ReportDataGrouping[]{};
    }
}
