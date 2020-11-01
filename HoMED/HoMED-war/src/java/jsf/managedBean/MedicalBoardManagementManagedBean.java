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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "medicalBoardManagementManagedBean")
@ViewScoped
public class MedicalBoardManagementManagedBean implements Serializable {

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    private List<MedicalBoardSlot> medicalBoardSlots;

    public MedicalBoardManagementManagedBean() {
        this.medicalBoardSlots = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
    }

    public List<MedicalBoardSlot> getMedicalBoardSlots() {
        return medicalBoardSlots;
    }

    public void setMedicalBoardSlots(List<MedicalBoardSlot> medicalBoardSlots) {
        this.medicalBoardSlots = medicalBoardSlots;
    }

    public String renderDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return dateFormat.format(date);
    }

}
