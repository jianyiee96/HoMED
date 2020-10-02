/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum FormInstanceStatusEnum {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    ARCHIVED("Archived");
    
    private String text;
    
    private FormInstanceStatusEnum(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}
