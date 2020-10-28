package util.enumeration;

public enum ReportDataValue {
    S_PES("PES Status"),
    S_BT("Blood Type"),
    S_CS("Bookings");

    private String text;

    private ReportDataValue(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    public static ReportDataValue[] getReportDataTypeValues(ReportDataType type) {
        if (type == ReportDataType.SERVICEMAN) { 
            return new ReportDataValue[]{S_PES,S_BT,S_CS};
        }
        return new ReportDataValue[]{S_PES};
    }
}
