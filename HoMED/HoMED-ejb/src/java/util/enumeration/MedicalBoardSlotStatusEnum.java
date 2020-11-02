/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum MedicalBoardSlotStatusEnum {

    // No cases have been allocated yet
    UNALLOCATED("Board Cases Unallocated"),
    // Cases have been allocated
    ALLOCATED("Board Cases Allocated"),
    // Board is currently ongoing
    ONGOING("Board Ongoing"),
    // Cases were allocated and board was already done
    COMPLETED("Board Completed"),
    // No cases were allocated and the slot has already expired, expiration at the end of the day [to be done]
    EXPIRED("Expired");

    private String stringVal;

    private MedicalBoardSlotStatusEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;

    }
}
