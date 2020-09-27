/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

/**
 *
 * @author sunag
 */
public enum FormInstanceStatusEnum {
    DRAFT("Draft"),
    PRE_CONSULTATION("Pre-consultation"),
    NON_BOARD_REVIEW("Non-board review"),
    PRE_BOARD_REVIEW("Pre-board review"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");
    
    private String text;
    
    private FormInstanceStatusEnum(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}
