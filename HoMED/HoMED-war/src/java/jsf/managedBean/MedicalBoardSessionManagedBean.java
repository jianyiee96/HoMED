/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.MedicalBoardCaseSessionBean;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.exceptions.SignMedicalBoardCaseException;

/**
 *
 * @author User
 */
@Named(value = "medicalBoardSessionManagedBean")
@ViewScoped
public class MedicalBoardSessionManagedBean implements Serializable {

    @EJB
    SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    MedicalBoardCaseSessionBeanLocal medicalBoardCaseSessionBeanLocal;

    MedicalBoardSlot medicalBoardSlot;

    MedicalBoardCase selectedCase;

    public MedicalBoardSessionManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

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
        } else {
            sortMedicalBoardCases(medicalBoardSlot.getMedicalBoardCases());
            if (medicalBoardSlot.getMedicalBoardCases().size() > 0) {
                selectedCase = medicalBoardSlot.getMedicalBoardCases().get(0);
            }
        }

    }

    public void sortMedicalBoardCases(List<MedicalBoardCase> mbs) {
        Comparator<MedicalBoardCase> comp = ((m1, m2) -> {

            if (!Objects.equals(m1.getIsSigned(), m2.getIsSigned())) {

                if (m1.getIsSigned()) {
                    return 1;
                } else {
                    return -1;
                }

            } else if (m1.getMedicalBoardType() != m2.getMedicalBoardType()) {

                if (m1.getMedicalBoardType() == MedicalBoardTypeEnum.ABSENCE) {
                    return 1;
                } else {
                    return -1;
                }

            } else {
                return m1.getConsultation().getEndDateTime().before(m2.getConsultation().getEndDateTime()) ? -1 : 1;
            }

        });
        Collections.sort(mbs, comp);

    }

    public void signSelectedCase() {

        if (selectedCase.getBoardFindings() == null) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to sign current case", "Please ensure board findings is not empty."));
        } else {

            try {
                medicalBoardCaseSessionBeanLocal.signMedicalBoardCase(selectedCase.getMedicalBoardCaseId(), selectedCase.getBoardFindings());
                medicalBoardSlot = slotSessionBeanLocal.retrieveMedicalBoardSlotById(medicalBoardSlot.getSlotId());
                medicalBoardSlot.getMedicalBoardCases().forEach(mbc -> {
                    if (mbc.equals(selectedCase)) {
                        selectedCase = mbc;
                    }
                });
                sortMedicalBoardCases(medicalBoardSlot.getMedicalBoardCases());
            } catch (SignMedicalBoardCaseException ex) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to sign current case", ex.getMessage()));

            }
        }
    }

    public String backgroundStyle(MedicalBoardCase mbc) {

        if (mbc.equals(selectedCase)) {
            return "x";
        } else {
            return mbc.getIsSigned() ? "sign-true" : "sign-false";
        }

    }

    public MedicalBoardSlot getMedicalBoardSlot() {
        return medicalBoardSlot;
    }

    public void setMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        this.medicalBoardSlot = medicalBoardSlot;
    }

    public MedicalBoardCase getSelectedCase() {
        return selectedCase;
    }

    public void setSelectedCase(MedicalBoardCase selectedCase) {
        this.selectedCase = selectedCase;
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
}
