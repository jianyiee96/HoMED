/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.classes;

import entity.MedicalBoardSlot;
import org.primefaces.model.ScheduleEvent;

public class MedicalBoardSlotWrapper implements Comparable<MedicalBoardSlotWrapper> {

    private Integer index;
    private Boolean isEditMode;
    private MedicalBoardSlot medicalBoardSlot;
    private ScheduleEvent scheduleEvent;

    public MedicalBoardSlotWrapper() {
        this.index = 0;
        this.isEditMode = Boolean.FALSE;
    }

    public MedicalBoardSlotWrapper(MedicalBoardSlot medicalBoardSlot, ScheduleEvent scheduleEvent) {
        this();
        this.medicalBoardSlot = medicalBoardSlot;
        this.scheduleEvent = scheduleEvent;
    }

    public MedicalBoardSlotWrapper(MedicalBoardSlot medicalBoardSlot) {
        this();
        this.medicalBoardSlot = medicalBoardSlot;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(Boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public MedicalBoardSlot getMedicalBoardSlot() {
        return medicalBoardSlot;
    }

    public void setMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        this.medicalBoardSlot = medicalBoardSlot;
    }

    public ScheduleEvent getScheduleEvent() {
        return scheduleEvent;
    }

    public void setScheduleEvent(ScheduleEvent scheduleEvent) {
        this.scheduleEvent = scheduleEvent;
    }

    @Override
    public int compareTo(MedicalBoardSlotWrapper another) {

        if (this.medicalBoardSlot.getStartDateTime().before(another.medicalBoardSlot.getStartDateTime())) {
            return -1;
        } else if (this.medicalBoardSlot.getStartDateTime().after(another.medicalBoardSlot.getStartDateTime())) {
            return 1;
        } else {
            return 0;
        }
    }

}
