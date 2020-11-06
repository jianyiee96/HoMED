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
import util.enumeration.MedicalBoardSlotStatusEnum;
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

    private Integer filterOption;
    private List<MedicalBoardSlot> filteredMedicalBoardSlots;

    private MedicalBoardSlot selectedMedicalBoardSlot;

    private List<MedicalBoardCase> medicalBoardInPresenceCases;
    private List<MedicalBoardCase> medicalBoardInAbsenceCases;

    private List<MedicalBoardCase> selectedMedicalBoardInPresenceCases;
    private List<MedicalBoardCase> selectedMedicalBoardInAbsenceCases;

    public MedicalBoardManagementManagedBean() {
        this.medicalBoardSlots = new ArrayList<>();

        this.filterOption = 1;
        this.filteredMedicalBoardSlots = new ArrayList<>();

        this.medicalBoardInPresenceCases = new ArrayList<>();
        this.medicalBoardInAbsenceCases = new ArrayList<>();

        this.selectedMedicalBoardInPresenceCases = new ArrayList<>();
        this.selectedMedicalBoardInAbsenceCases = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        doFilterMedicalBoards();
    }

    public void doFilterMedicalBoards() {
        this.medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
        this.filteredMedicalBoardSlots.clear();

        // 0 - Past
        // 1 - Upcoming
        if (this.filterOption == 0) {
            this.medicalBoardSlots.forEach(mbs -> {
                if (mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.COMPLETED || mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.EXPIRED) {
                    this.filteredMedicalBoardSlots.add(mbs);
                }
            });
        } else {
            this.medicalBoardSlots.forEach(mbs -> {
                if (mbs.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.COMPLETED && mbs.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.EXPIRED) {
                    this.filteredMedicalBoardSlots.add(mbs);
                }
            });
        }
    }

    public void saveChanges() {
        List<MedicalBoardCase> selectedMedicalBoardCases = new ArrayList<>();
        selectedMedicalBoardCases.addAll(selectedMedicalBoardInPresenceCases);
        selectedMedicalBoardCases.addAll(selectedMedicalBoardInAbsenceCases);

        try {
            medicalBoardCaseSessionBeanLocal.allocateMedicalBoardCasesToMedicalBoardSlot(selectedMedicalBoardSlot, selectedMedicalBoardCases);

            doFilterMedicalBoards();
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

        this.selectedMedicalBoardInAbsenceCases.clear();
        this.selectedMedicalBoardInPresenceCases.clear();

        if (this.selectedMedicalBoardSlot != null) {
            this.selectedMedicalBoardSlot = slotSessionBeanLocal.retrieveMedicalBoardSlotById(this.selectedMedicalBoardSlot.getSlotId());
        }

        this.selectedMedicalBoardSlot = medicalBoardSlot;

        // MBIA
        this.selectedMedicalBoardInAbsenceCases = medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.ABSENCE, medicalBoardSlot);
        this.medicalBoardInAbsenceCases.addAll(selectedMedicalBoardInAbsenceCases);
        this.medicalBoardInAbsenceCases.addAll(medicalBoardCaseSessionBeanLocal.retrieveUnallocatedMedicalBoardCases(MedicalBoardTypeEnum.ABSENCE));
        // MBIP
        this.selectedMedicalBoardInPresenceCases = medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.PRESENCE, medicalBoardSlot);
        this.medicalBoardInPresenceCases.addAll(selectedMedicalBoardInPresenceCases);
        this.medicalBoardInPresenceCases.addAll(medicalBoardCaseSessionBeanLocal.retrieveUnallocatedMedicalBoardCases(MedicalBoardTypeEnum.PRESENCE));

    }

    public List<MedicalBoardCase> getMedicalBoardInPresenceCasesForSelectedMedicalBoardSlot() {
        return medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.PRESENCE, selectedMedicalBoardSlot);
    }

    public List<MedicalBoardCase> getMedicalBoardInAbsenceCasesForSelectedMedicalBoardSlot() {
        return medicalBoardCaseSessionBeanLocal.retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum.ABSENCE, selectedMedicalBoardSlot);
    }

    public String renderDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy kk:mm");
        return dateFormat.format(date);
    }

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
            case UNASSIGNED:
                return "#4ea279";
            case ASSIGNED:
                return "#0d6e63";
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
            timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase() * this.selectedMedicalBoardInPresenceCases.size();
        } else {
            timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase() * this.selectedMedicalBoardInAbsenceCases.size();
        }

        return getDuration(timeNeededInMins);
    }

    public String calculateTotalEstimatedTimeNeeded() {
        Long timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase() * this.selectedMedicalBoardInPresenceCases.size() + (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase() * this.selectedMedicalBoardInAbsenceCases.size();
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
        Long timeNeededInMins = (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase() * this.selectedMedicalBoardInPresenceCases.size() + (long) this.selectedMedicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase() * this.selectedMedicalBoardInAbsenceCases.size();
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

    public Integer getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(Integer filterOption) {
        this.filterOption = filterOption;
    }

    public List<MedicalBoardSlot> getFilteredMedicalBoardSlots() {
        return filteredMedicalBoardSlots;
    }

    public void setFilteredMedicalBoardSlots(List<MedicalBoardSlot> filteredMedicalBoardSlots) {
        this.filteredMedicalBoardSlots = filteredMedicalBoardSlots;
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

    public List<MedicalBoardCase> getSelectedMedicalBoardInPresenceCases() {
        return selectedMedicalBoardInPresenceCases;
    }

    public void setSelectedMedicalBoardInPresenceCases(List<MedicalBoardCase> selectedMedicalBoardInPresenceCases) {
        this.selectedMedicalBoardInPresenceCases = selectedMedicalBoardInPresenceCases;
    }

    public List<MedicalBoardCase> getSelectedMedicalBoardInAbsenceCases() {
        return selectedMedicalBoardInAbsenceCases;
    }

    public void setSelectedMedicalBoardInAbsenceCases(List<MedicalBoardCase> selectedMedicalBoardInAbsenceCases) {
        this.selectedMedicalBoardInAbsenceCases = selectedMedicalBoardInAbsenceCases;
    }

}
