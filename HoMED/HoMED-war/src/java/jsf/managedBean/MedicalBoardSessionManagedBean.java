/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.SlotSessionBeanLocal;
import entity.MedicalBoardSlot;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.exceptions.StartMedicalBoardSessionException;

/**
 *
 * @author User
 */
@Named(value = "medicalBoardSessionManagedBean")
@ViewScoped
public class MedicalBoardSessionManagedBean implements Serializable {

    @EJB
    SlotSessionBeanLocal slotSessionBeanLocal;

    MedicalBoardSlot medicalBoardSlot;

    String medicalOfficerOneKey;

    String medicalOfficerTwoKey;

    public MedicalBoardSessionManagedBean() {
        medicalOfficerOneKey = "";
        medicalOfficerTwoKey = "";
    }

    @PostConstruct
    public void postConstruct() {

        System.out.println("Session post construct called");

        try {
            Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            medicalBoardSlot = (MedicalBoardSlot) flash.get("medicalBoardSlot");

        } catch (NullPointerException ex) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board.xhtml");
            } catch (IOException exx) {
                System.out.println(exx);
            }
        }

        if (medicalBoardSlot == null
                || medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.UNASSIGNED
                || medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.ASSIGNED
                || medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.ALLOCATED
                || medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.EXPIRED) {

            try {
                System.out.println("Invalid Medical Board Slot");
                FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board.xhtml");
            } catch (IOException ex) {
                System.out.println("Fail to redirect: " + ex);
            }
        }

    }

    public void startCurrentMedicalBoard() {

        System.out.println(medicalOfficerOneKey);
        System.out.println(medicalOfficerTwoKey);

        try {

            if (medicalBoardSlot.getMedicalOfficerOneKey().equals(medicalOfficerOneKey) && medicalBoardSlot.getMedicalOfficerTwoKey().equals(medicalOfficerTwoKey)) {
                System.out.println("Able to start session");
                slotSessionBeanLocal.startMedicalBoardSession(medicalBoardSlot.getSlotId());
                medicalBoardSlot = slotSessionBeanLocal.retrieveMedicalBoardSlotById(medicalBoardSlot.getSlotId());
            } else {
                System.out.println("Unable to start session");
            }
        } catch (StartMedicalBoardSessionException ex) {
            System.out.println("Unable to start: " + ex);
        }
    }

    public MedicalBoardSlot getMedicalBoardSlot() {
        return medicalBoardSlot;
    }

    public void setMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        this.medicalBoardSlot = medicalBoardSlot;
    }

    public String getMedicalOfficerOneKey() {
        return medicalOfficerOneKey;
    }

    public void setMedicalOfficerOneKey(String medicalOfficerOneKey) {
        this.medicalOfficerOneKey = medicalOfficerOneKey;
    }

    public String getMedicalOfficerTwoKey() {
        return medicalOfficerTwoKey;
    }

    public void setMedicalOfficerTwoKey(String medicalOfficerTwoKey) {
        this.medicalOfficerTwoKey = medicalOfficerTwoKey;
    }

}
