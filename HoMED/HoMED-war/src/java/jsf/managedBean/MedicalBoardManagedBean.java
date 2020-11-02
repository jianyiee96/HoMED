/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Employee;
import entity.MedicalBoardSlot;
import entity.MedicalOfficer;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "medicalBoardManagedBean")
@ViewScoped
public class MedicalBoardManagedBean implements Serializable {

    @EJB
    private SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<MedicalBoardSlot> allMedicalBoardSlots;

    private List<MedicalBoardSlot> todayMedicalBoardSlots;

    private List<MedicalBoardSlot> relevantMedicalBoardSlots;

    private MedicalOfficer currentMedicalOfficer;

    public MedicalBoardManagedBean() {
        this.allMedicalBoardSlots = new ArrayList<>();
        this.relevantMedicalBoardSlots = new ArrayList<>();
        this.todayMedicalBoardSlots = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {

        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(currentEmployee.getEmployeeId());
        }

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        this.allMedicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
        for (MedicalBoardSlot mbs : allMedicalBoardSlots) {
            if (mbs.getMembersOfBoard().contains(currentMedicalOfficer)) {

                Calendar mbsTime = Calendar.getInstance();
                mbsTime.setTime(mbs.getStartDateTime());

                boolean sameDay = mbsTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                        && mbsTime.get(Calendar.YEAR) == now.get(Calendar.YEAR);

                if (sameDay) {
                    todayMedicalBoardSlots.add(mbs);
                } else {
                    relevantMedicalBoardSlots.add(mbs);
                }
            }
        }

        System.out.println(allMedicalBoardSlots);
        System.out.println(relevantMedicalBoardSlots);

        System.out.println("Current medical officer: " + currentMedicalOfficer.getName());
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

    public List<MedicalBoardSlot> getTodayMedicalBoardSlots() {
        return todayMedicalBoardSlots;
    }

    public void setTodayMedicalBoardSlots(List<MedicalBoardSlot> todayMedicalBoardSlots) {
        this.todayMedicalBoardSlots = todayMedicalBoardSlots;
    }

    public String renderDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return dateFormat.format(date);
    }

    public int renderTotalTime(MedicalBoardSlot mbs) {
        return (mbs.getEstimatedTimeForEachBoardInAbsenceCase() * mbs.getMedicalBoardInAbsenceCases().size()
                + mbs.getEstimatedTimeForEachBoardInPresenceCase() * mbs.getMedicalBoardInPresenceCases().size());
    }

}
