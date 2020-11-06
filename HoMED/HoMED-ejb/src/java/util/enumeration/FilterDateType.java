package util.enumeration;

public enum FilterDateType {
    NONE("None"),
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year"),
    CUSTOM("Custom");

    private String text;

    private FilterDateType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
