/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.classes;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.MedicalBoardSlot;
import entity.MedicalOfficer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.primefaces.model.ScheduleEvent;

public class MedicalBoardSlotWrapper implements Comparable<MedicalBoardSlotWrapper> {

    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Integer index;
    private Boolean isEditMode;
    private MedicalBoardSlot medicalBoardSlot;
    private ScheduleEvent scheduleEvent;

    private Long chairmanId;
    private Long medicalOfficerOneId;
    private Long medicalOfficerTwoId;

    public MedicalBoardSlotWrapper() {
        this.employeeSessionBeanLocal = lookupEmployeeSessionBeanLocal();
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

    public List<MedicalOfficer> getUnallocatedChairmen() {
        List<MedicalOfficer> chairmen = employeeSessionBeanLocal.retrieveChairmen();
        List<MedicalOfficer> chairmenToRemove = new ArrayList<>();

        for (MedicalOfficer mo : chairmen) {
            if (mo.getEmployeeId() == medicalOfficerOneId || mo.getEmployeeId() == medicalOfficerTwoId) {
                chairmenToRemove.add(mo);
            }
        }

        for (MedicalOfficer mo : chairmenToRemove) {
            chairmen.remove(mo);
        }

        return chairmen;
    }

    public List<MedicalOfficer> getUnallocatedMedicalOfficersOne() {
        List<MedicalOfficer> medicalOfficers = employeeSessionBeanLocal.retrieveMedicalOfficers();
        List<MedicalOfficer> medicalOfficersToRemove = new ArrayList<>();

        for (MedicalOfficer mo : medicalOfficers) {
            if (mo.getEmployeeId() == chairmanId || mo.getEmployeeId() == medicalOfficerTwoId) {
                medicalOfficersToRemove.add(mo);
            }
        }

        for (MedicalOfficer mo : medicalOfficersToRemove) {
            medicalOfficers.remove(mo);
        }

        return medicalOfficers;
    }

    public List<MedicalOfficer> getUnallocatedMedicalOfficersTwo() {
        List<MedicalOfficer> medicalOfficers = employeeSessionBeanLocal.retrieveMedicalOfficers();
        List<MedicalOfficer> medicalOfficersToRemove = new ArrayList<>();

        for (MedicalOfficer mo : medicalOfficers) {
            if (mo.getEmployeeId() == chairmanId || mo.getEmployeeId() == medicalOfficerOneId) {
                medicalOfficersToRemove.add(mo);
            }
        }

        for (MedicalOfficer mo : medicalOfficersToRemove) {
            medicalOfficers.remove(mo);
        }

        return medicalOfficers;
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

    public Long getChairmanId() {
        return chairmanId;
    }

    public void setChairmanId(Long chairmanId) {
        this.chairmanId = chairmanId;
    }

    public Long getMedicalOfficerOneId() {
        return medicalOfficerOneId;
    }

    public void setMedicalOfficerOneId(Long medicalOfficerOneId) {
        this.medicalOfficerOneId = medicalOfficerOneId;
    }

    public Long getMedicalOfficerTwoId() {
        return medicalOfficerTwoId;
    }

    public void setMedicalOfficerTwoId(Long medicalOfficerTwoId) {
        this.medicalOfficerTwoId = medicalOfficerTwoId;
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

    private EmployeeSessionBeanLocal lookupEmployeeSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (EmployeeSessionBeanLocal) c.lookup("java:global/HoMED/HoMED-ejb/EmployeeSessionBean!ejb.session.stateless.EmployeeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
