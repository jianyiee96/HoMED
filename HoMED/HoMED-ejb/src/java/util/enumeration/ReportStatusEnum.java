package util.enumeration;

public enum ReportStatusEnum {
    PERSONAL("Personal"),
    PUBLISHED("Published"),
    ARCHIVED("Archived");
    
    private String text;

    private ReportStatusEnum(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}
