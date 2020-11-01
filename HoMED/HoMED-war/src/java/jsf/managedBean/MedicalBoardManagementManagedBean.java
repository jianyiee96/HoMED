/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.MedicalBoardSlotStatusEnum;

@Named(value = "medicalBoardManagementManagedBean")
@ViewScoped
public class MedicalBoardManagementManagedBean implements Serializable {

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;
    @EJB(name = "MedicalBoardCaseSessionBeanLocal")
    private MedicalBoardCaseSessionBeanLocal medicalBoardCaseSessionBeanLocal;

    private List<MedicalBoardSlot> medicalBoardSlots;

    private MedicalBoardSlot selectedMedicalBoardSlot;

    private List<MedicalBoardCase> medicalBoardInPresenceCases;
    private List<MedicalBoardCase> medicalBoardInAbsenceCases;

    private List<Integer> selectedMedicalBoardInPresenceCaseIds;
    private List<Integer> selectedMedicalBoardInAbsenceCaseIds;

    public MedicalBoardManagementManagedBean() {
        this.medicalBoardSlots = new ArrayList<>();

        this.selectedMedicalBoardInPresenceCaseIds = new ArrayList<>();
        this.selectedMedicalBoardInAbsenceCaseIds = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();

        this.medicalBoardInPresenceCases = medicalBoardCaseSessionBeanLocal.retrieveUnassignedMedicalBoardInPresenceCases();
        this.medicalBoardInAbsenceCases = medicalBoardCaseSessionBeanLocal.retrieveUnassignedMedicalBoardInAbsenceCases();
    }

    public String renderDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return dateFormat.format(date);
    }

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public String renderTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        return dateFormat.format(date);
    }

    public String renderDuration(Date start, Date end) {
        Long diffInMillies = Math.abs(end.getTime() - start.getTime());
        Long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

        Long hours = diffInMinutes / 60;
        Long minutes = diffInMinutes % 60;

        String duration = "";
        if (hours != 0) {
            duration += hours;
            if (hours == 1) {
                duration += " hour ";
            } else {
                duration += " hours ";
            }
        }

        if (minutes != 0) {
            duration += minutes;
            if (minutes == 1) {
                duration += " minute";
            } else {
                duration += " minutes";
            }
        }

        return duration;
    }

    public String getCommandLinkColour(MedicalBoardSlot medicalBoardSlot) {
        switch (medicalBoardSlot.getMedicalBoardSlotStatusEnum()) {
            case UNALLOCATED:
                return "#4ea279";
            case ALLOCATED:
                return "#6b6bb8";
            case ONGOING:
                return "#C76D09";
            case COMPLETED:
                return "#C0C0E1";
            default:
                return "#55555A";
        }
    }

    public String calculateEstimatedTimeNeeded(Boolean isMBIP) {
        Long timeNeededInMins = 0l;

        if (isMBIP) {
            timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase() * this.selectedMedicalBoardInPresenceCaseIds.size();
        } else {
            timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase() * this.selectedMedicalBoardInAbsenceCaseIds.size();
        }

        return getDuration(timeNeededInMins);
    }

    public String calculateTotalEstimatedTimeNeeded() {
        Long timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase() * this.selectedMedicalBoardInPresenceCaseIds.size() + (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase() * this.selectedMedicalBoardInAbsenceCaseIds.size();
        return getDuration(timeNeededInMins);
    }

    private String getDuration(Long timeNeededInMins) {
        Long hours = timeNeededInMins / 60;
        Long minutes = timeNeededInMins % 60;

        String duration = "";
        if (hours != 0) {
            duration += hours;
            if (hours == 1) {
                duration += " hour ";
            } else {
                duration += " hours ";
            }
        }

        if (minutes != 0) {
            duration += minutes;
            if (minutes == 1) {
                duration += " minute";
            } else {
                duration += " minutes";
            }
        }

        if (duration.equals("")) {
            return "N.A.";
        }

        return duration;
    }

    public Date calculateEstimatedEndDate() {
        Long timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase() * this.selectedMedicalBoardInPresenceCaseIds.size() + (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase() * this.selectedMedicalBoardInAbsenceCaseIds.size();

        Long minuteInMillis = 60000l; //millisecs

        Calendar date = Calendar.getInstance();
        date.setTime(this.selectedMedicalBoardSlot.getStartDateTime());
        Long dateInMillis = date.getTimeInMillis();
        
        return new Date(dateInMillis + (timeNeededInMins * minuteInMillis));
    }
    
    public void saveChanges() {
        
    }
    
    public void revertChanges() {
        
    }

    public List<MedicalBoardSlot> getMedicalBoardSlots() {
        return medicalBoardSlots;
    }

    public void setMedicalBoardSlots(List<MedicalBoardSlot> medicalBoardSlots) {
        this.medicalBoardSlots = medicalBoardSlots;
    }

    public MedicalBoardSlot getSelectedMedicalBoardSlot() {
        return selectedMedicalBoardSlot;
    }

    public void setSelectedMedicalBoardSlot(MedicalBoardSlot selectedMedicalBoardSlot) {
        this.selectedMedicalBoardSlot = selectedMedicalBoardSlot;
    }

    public List<MedicalBoardCase> getMedicalBoardInPresenceCases() {
        return medicalBoardInPresenceCases;
    }

    public void setMedicalBoardInPresenceCases(List<MedicalBoardCase> medicalBoardInPresenceCases) {
        this.medicalBoardInPresenceCases = medicalBoardInPresenceCases;
    }

    public List<MedicalBoardCase> getMedicalBoardInAbsenceCases() {
        return medicalBoardInAbsenceCases;
    }

    public void setMedicalBoardInAbsenceCases(List<MedicalBoardCase> medicalBoardInAbsenceCases) {
        this.medicalBoardInAbsenceCases = medicalBoardInAbsenceCases;
    }

    public List<Integer> getSelectedMedicalBoardInPresenceCaseIds() {
        return selectedMedicalBoardInPresenceCaseIds;
    }

    public void setSelectedMedicalBoardInPresenceCaseIds(List<Integer> selectedMedicalBoardInPresenceCaseIds) {
        this.selectedMedicalBoardInPresenceCaseIds = selectedMedicalBoardInPresenceCaseIds;
    }

    public List<Integer> getSelectedMedicalBoardInAbsenceCaseIds() {
        return selectedMedicalBoardInAbsenceCaseIds;
    }

    public void setSelectedMedicalBoardInAbsenceCaseIds(List<Integer> selectedMedicalBoardInAbsenceCaseIds) {
        this.selectedMedicalBoardInAbsenceCaseIds = selectedMedicalBoardInAbsenceCaseIds;
    }

}
