/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum FormStatusEnum {
    DRAFT("Draft"),
    PUBLISHED("Published"),
    DELETED("Deleted"),
    ARCHIVED("Archived");
    
    private String text;

    private FormStatusEnum(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
}
