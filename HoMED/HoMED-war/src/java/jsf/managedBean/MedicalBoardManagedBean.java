/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Employee;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import entity.MedicalOfficer;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.exceptions.StartMedicalBoardSessionException;

@Named(value = "medicalBoardManagedBean")
@ViewScoped
public class MedicalBoardManagedBean implements Serializable {

    @EJB
    private SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private MedicalBoardSlot selectedMedicalBoardSlot;

    private List<MedicalBoardSlot> allMedicalBoardSlots;

    private List<MedicalBoardSlot> filteredMedicalBoardSlots;

    private List<MedicalBoardSlot> relevantMedicalBoardSlots;

    private MedicalOfficer currentMedicalOfficer;

    private Integer filterOption;

    private Calendar now;

    private Boolean chairmanView;
    private Boolean mo1View;
    private Boolean mo2View;

    private String mo1keyInput;
    private String mo2keyInput;

    public MedicalBoardManagedBean() {
        this.allMedicalBoardSlots = new ArrayList<>();
        this.relevantMedicalBoardSlots = new ArrayList<>();
        this.filteredMedicalBoardSlots = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {

        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(currentEmployee.getEmployeeId());
        }

        now = Calendar.getInstance();
        now.setTime(new Date());

        this.allMedicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
        for (MedicalBoardSlot mbs : allMedicalBoardSlots) {
            if (mbs.getMembersOfBoard().contains(currentMedicalOfficer) && mbs.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.UNASSIGNED) {

                if (isToday(mbs.getStartDateTime())) {
                    filteredMedicalBoardSlots.add(mbs);
                }

                relevantMedicalBoardSlots.add(mbs);

            }
        }

        this.filterOption = 0;
        this.doFilterMedicalBoards();

        if (this.filteredMedicalBoardSlots.isEmpty()) {
            this.filterOption = 1;
            this.doFilterMedicalBoards();

            if (this.filteredMedicalBoardSlots.isEmpty()) {
                this.filterOption = 2;
                this.doFilterMedicalBoards();
            }

        } else {
            this.selectMedicalBoardSlot(this.filteredMedicalBoardSlots.get(0));
        }

    }

    public void selectMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        this.selectedMedicalBoardSlot = medicalBoardSlot;

        mo1keyInput = "";
        mo2keyInput = "";
        chairmanView = false;
        mo1View = false;
        mo2View = false;

        if (medicalBoardSlot.getChairman().equals(this.currentMedicalOfficer)) {
            chairmanView = true;
        } else if (medicalBoardSlot.getMedicalOfficerOne().equals(this.currentMedicalOfficer)) {
            mo1View = true;
        } else if (medicalBoardSlot.getMedicalOfficerTwo().equals(this.currentMedicalOfficer)) {
            mo2View = true;
        }

        Comparator<MedicalBoardCase> sortByTypeThenDate = ((c1, c2) -> {
            if (c1.getMedicalBoardType() == c2.getMedicalBoardType()) {

                if (c1.getConsultation().getEndDateTime().before(c2.getConsultation().getEndDateTime())) {
                    return -1;
                } else {
                    return 1;
                }

            } else if (c1.getMedicalBoardType() == MedicalBoardTypeEnum.PRESENCE) {
                return 1;
            } else {
                return -1;
            }
        });
        Collections.sort(this.selectedMedicalBoardSlot.getMedicalBoardCases(), sortByTypeThenDate);

    }

    public void startSelectedMedicalBoard() {

        if (mo1keyInput == null || mo1keyInput.isEmpty() || mo2keyInput == null || mo2keyInput.isEmpty()) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to start medical board", "Please supply both MO1 and MO2 keys"));
        } else if (this.selectedMedicalBoardSlot.getMedicalOfficerOneKey().equals(mo1keyInput)
                && this.selectedMedicalBoardSlot.getMedicalOfficerTwoKey().equals(mo2keyInput)) {

            try {

                slotSessionBeanLocal.startMedicalBoardSession(this.selectedMedicalBoardSlot.getSlotId());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Started Medical Board!", "Medical board has started!"));

                redirectMedicalBoardSession(this.selectedMedicalBoardSlot);

            } catch (StartMedicalBoardSessionException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to start medical board", ex.getMessage()));

            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to start medical board", "Invalid keys supplied"));

        }
    }

    public void redirectMedicalBoardSession(MedicalBoardSlot medicalBoardSlot) {

        if (currentMedicalOfficer.getIsChairman() == false || !medicalBoardSlot.getChairman().equals(currentMedicalOfficer)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", "Current Board Session can only be accessed by the chairman of the board."));
            return;
        }

        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("medicalBoardSlot", medicalBoardSlot);

        try {

            FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board-session.xhtml");

        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));

        }

    }

    public void doFilterMedicalBoards() {
        this.filteredMedicalBoardSlots.clear();

        if (this.filterOption == 0) { //Today
            this.relevantMedicalBoardSlots.forEach(mbs -> {

                if (isToday(mbs.getStartDateTime())) {
                    this.filteredMedicalBoardSlots.add(mbs);
                }

            });
        } else if (this.filterOption == 1) { //Past

            this.relevantMedicalBoardSlots.forEach(mbs -> {
                if (mbs.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.COMPLETED && mbs.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.EXPIRED) {
                    this.filteredMedicalBoardSlots.add(mbs);
                }
            });
        } else { //Upcoming

            this.relevantMedicalBoardSlots.forEach(mbs -> {

                if (mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.COMPLETED || mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.EXPIRED) {
                    this.filteredMedicalBoardSlots.add(mbs);
                }

            });
        }

    }

    public MedicalBoardSlot getSelectedMedicalBoardSlot() {
        return selectedMedicalBoardSlot;
    }

    public void setSelectedMedicalBoardSlot(MedicalBoardSlot selectedMedicalBoardSlot) {
        this.selectedMedicalBoardSlot = selectedMedicalBoardSlot;
    }

    public MedicalOfficer getCurrentMedicalOfficer() {
        return currentMedicalOfficer;
    }

    public void setCurrentMedicalOfficer(MedicalOfficer currentMedicalOfficer) {
        this.currentMedicalOfficer = currentMedicalOfficer;
    }

    public List<MedicalBoardSlot> getRelevantMedicalBoardSlots() {
        return relevantMedicalBoardSlots;
    }

    public void setRelevantMedicalBoardSlots(List<MedicalBoardSlot> relevantMedicalBoardSlots) {
        this.relevantMedicalBoardSlots = relevantMedicalBoardSlots;
    }

    public List<MedicalBoardSlot> getFilteredMedicalBoardSlots() {
        return filteredMedicalBoardSlots;
    }

    public void setFilteredMedicalBoardSlots(List<MedicalBoardSlot> filteredMedicalBoardSlots) {
        this.filteredMedicalBoardSlots = filteredMedicalBoardSlots;
    }

    public Integer getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(Integer filterOption) {
        this.filterOption = filterOption;
    }

    public Boolean getChairmanView() {
        return chairmanView;
    }

    public void setChairmanView(Boolean chairmanView) {
        this.chairmanView = chairmanView;
    }

    public Boolean getMo1View() {
        return mo1View;
    }

    public void setMo1View(Boolean mo1View) {
        this.mo1View = mo1View;
    }

    public Boolean getMo2View() {
        return mo2View;
    }

    public void setMo2View(Boolean mo2View) {
        this.mo2View = mo2View;
    }

    public String getMo1keyInput() {
        return mo1keyInput;
    }

    public void setMo1keyInput(String mo1keyInput) {
        this.mo1keyInput = mo1keyInput;
    }

    public String getMo2keyInput() {
        return mo2keyInput;
    }

    public void setMo2keyInput(String mo2keyInput) {
        this.mo2keyInput = mo2keyInput;
    }

    public String renderDateTime(Date date) {
        if (date == null) {
            return "N.A.";
        }

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy kk:mm");
        return dateFormat.format(date);
    }

    public String renderDate(Date date) {
        if (date == null) {
            return "N.A.";
        }

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public String renderTime(Date date) {
        if (date == null) {
            return "N.A.";
        }

        DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        return dateFormat.format(date);
    }

    public boolean isToday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && c.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    public String getMedicalOfficerRole(MedicalBoardSlot mbs) {

        if (mbs.getChairman().equals(currentMedicalOfficer)) {
            return "Chairman";
        } else if (mbs.getMedicalOfficerOne().equals(currentMedicalOfficer)) {
            return "Medical Officer 2";
        } else {
            return "Medical Officer 1";
        }

    }

    public int renderTotalTime(MedicalBoardSlot mbs) {
        return (mbs.getEstimatedTimeForEachBoardInAbsenceCase() * mbs.getMedicalBoardInAbsenceCases().size()
                + mbs.getEstimatedTimeForEachBoardInPresenceCase() * mbs.getMedicalBoardInPresenceCases().size());
    }

}
