/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum FormTemplateStatusEnum {
    DRAFT("Draft"),
    PUBLISHED("Published"),
    DELETED("Deleted"),
    ARCHIVED("Archived");
    
    private String text;

    private FormTemplateStatusEnum(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
}
