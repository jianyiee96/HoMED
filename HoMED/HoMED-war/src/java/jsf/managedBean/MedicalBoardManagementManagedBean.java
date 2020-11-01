/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.SlotSessionBeanLocal;
import entity.MedicalBoardSlot;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private List<MedicalBoardSlot> medicalBoardSlots;

    private MedicalBoardSlot selectedMedicalBoardSlot;

    public MedicalBoardManagementManagedBean() {
        this.medicalBoardSlots = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
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

}
