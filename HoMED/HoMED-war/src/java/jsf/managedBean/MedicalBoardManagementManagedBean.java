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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import util.enumeration.MedicalBoardTypeEnum;
import util.exceptions.UpdateMedicalBoardSlotException;

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

    private List<Long> selectedMedicalBoardInPresenceCaseIds;
    private List<Long> selectedMedicalBoardInAbsenceCaseIds;

    public MedicalBoardManagementManagedBean() {
        this.medicalBoardSlots = new ArrayList<>();

        this.medicalBoardInPresenceCases = new ArrayList<>();
        this.medicalBoardInAbsenceCases = new ArrayList<>();

        this.selectedMedicalBoardInPresenceCaseIds = new ArrayList<>();
        this.selectedMedicalBoardInAbsenceCaseIds = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
    }

    public void saveChanges() {
        List<Long> medicalBoardCaseIds = new ArrayList<>();

        medicalBoardCaseIds.addAll(selectedMedicalBoardInPresenceCaseIds);
        medicalBoardCaseIds.addAll(selectedMedicalBoardInAbsenceCaseIds);

        try {
            medicalBoardCaseSessionBeanLocal.allocateMedicalBoardCasesToMedicalBoardSlot(selectedMedicalBoardSlot, medicalBoardCaseIds);

            this.medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
            revertChanges(selectedMedicalBoardSlot);
            PrimeFaces.current().ajax().update("formMedicalBoardManagement");
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Board Cases Allocated", "Medical Board Cases have been allocated successfully!"));
        } catch (UpdateMedicalBoardSlotException ex) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Board Management", ex.getMessage()));
        }
    }

    public void selectMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        revertChanges(medicalBoardSlot);
    }

    public void revertChanges(MedicalBoardSlot medicalBoardSlot) {
        this.medicalBoardInAbsenceCases.clear();
        this.medicalBoardInPresenceCases.clear();

        this.selectedMedicalBoardInAbsenceCaseIds.clear();
        this.selectedMedicalBoardInPresenceCaseIds.clear();

        if (this.selectedMedicalBoardSlot != null) {
            this.selectedMedicalBoardSlot = slotSessionBeanLocal.retrieveMedicalBoardSlotById(this.selectedMedicalBoardSlot.getSlotId());
        }

        this.selectedMedicalBoardSlot = medicalBoardSlot;

        // MBIA
        List<MedicalBoardCase> selectedMedicalBoardInAbsenceCases = medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.ABSENCE, medicalBoardSlot);
        selectedMedicalBoardInAbsenceCases.forEach(mbiaCase -> {
            this.selectedMedicalBoardInAbsenceCaseIds.add(mbiaCase.getMedicalBoardCaseId());
        });

        this.medicalBoardInAbsenceCases.addAll(selectedMedicalBoardInAbsenceCases);
        this.medicalBoardInAbsenceCases.addAll(medicalBoardCaseSessionBeanLocal.retrieveUnassignedMedicalBoardCases(MedicalBoardTypeEnum.ABSENCE));

        // MBIP
        List<MedicalBoardCase> selectedMedicalBoardInPresenceCases = medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.PRESENCE, medicalBoardSlot);
        selectedMedicalBoardInPresenceCases.forEach(mbipCase -> {
            this.selectedMedicalBoardInPresenceCaseIds.add(mbipCase.getMedicalBoardCaseId());
        });

        this.medicalBoardInPresenceCases.addAll(selectedMedicalBoardInPresenceCases);
        this.medicalBoardInPresenceCases.addAll(medicalBoardCaseSessionBeanLocal.retrieveUnassignedMedicalBoardCases(MedicalBoardTypeEnum.PRESENCE));
    }

    public List<MedicalBoardCase> getMedicalBoardInPresenceCasesForSelectedMedicalBoardSlot() {
        return medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.PRESENCE, selectedMedicalBoardSlot);
    }

    public List<MedicalBoardCase> getMedicalBoardInAbsenceCasesForSelectedMedicalBoardSlot() {
        return medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.ABSENCE, selectedMedicalBoardSlot);
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
        return getDuration(diffInMinutes);
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

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage("growl-message", message);
        PrimeFaces.current().ajax().update("growl-message");
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

    public List<Long> getSelectedMedicalBoardInPresenceCaseIds() {
        return selectedMedicalBoardInPresenceCaseIds;
    }

    public void setSelectedMedicalBoardInPresenceCaseIds(List<Long> selectedMedicalBoardInPresenceCaseIds) {
        this.selectedMedicalBoardInPresenceCaseIds = selectedMedicalBoardInPresenceCaseIds;
    }

    public List<Long> getSelectedMedicalBoardInAbsenceCaseIds() {
        return selectedMedicalBoardInAbsenceCaseIds;
    }

    public void setSelectedMedicalBoardInAbsenceCaseIds(List<Long> selectedMedicalBoardInAbsenceCaseIds) {
        this.selectedMedicalBoardInAbsenceCaseIds = selectedMedicalBoardInAbsenceCaseIds;
    }

}
