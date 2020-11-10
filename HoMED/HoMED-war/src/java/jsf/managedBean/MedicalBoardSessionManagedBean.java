/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ConditionStatusSessionBeanLocal;
import ejb.session.stateless.MedicalBoardCaseSessionBean;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.ConditionStatus;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import entity.PreDefinedConditionStatus;
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
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.enumeration.PesStatusEnum;
import util.exceptions.CreateMedicalBoardCaseException;
import util.exceptions.EndMedicalBoardSessionException;
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

    @EJB
    ConditionStatusSessionBeanLocal conditionStatusSessionBeanLocal;

    MedicalBoardSlot medicalBoardSlot;

    MedicalBoardCase selectedCase;

    List<ConditionStatus> servicemanCurrentConditionStatuses;

    List<PreDefinedConditionStatus> preDefinedConditionStatuses;

    String newPreDefinedConditionStatus;

    Boolean hasFollowUp;

    String currentFollowUpAction;

    String followUpStatement;

    public MedicalBoardSessionManagedBean() {
        servicemanCurrentConditionStatuses = new ArrayList<>();
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
                FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board.xhtml");
            } catch (IOException ex) {
                System.out.println("Fail to redirect: " + ex);
            }
        } else {

            this.preDefinedConditionStatuses = conditionStatusSessionBeanLocal.retrieveAllPreDefinedConditionStatus();
            sortMedicalBoardCases(medicalBoardSlot.getMedicalBoardCases());
            if (medicalBoardSlot.getMedicalBoardCases().size() > 0) {
                hasFollowUp = false;
                selectedCase = medicalBoardSlot.getMedicalBoardCases().get(0);
                servicemanCurrentConditionStatuses = conditionStatusSessionBeanLocal.retrieveActiveConditionStatusByServiceman(this.selectedCase.getConsultation().getBooking().getServiceman().getServicemanId());

            }
        }

    }

    public void back() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board.xhtml");
        } catch (IOException ex) {
            System.out.println("Fail to redirect: " + ex);
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
        } else if (hasFollowUp && (followUpStatement == null || followUpStatement.equals(""))) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to sign current case", "Follow up medical board is specified. Please ensure follow up statement of case is not empty."));

        } else {
            try {
                clearEmptyConditionStatus();

                if (hasFollowUp) {
                    if (this.currentFollowUpAction.equals("MBIP")) {
                        medicalBoardCaseSessionBeanLocal.createMedicalBoardCaseByBoard(selectedCase.getMedicalBoardCaseId(), MedicalBoardTypeEnum.PRESENCE, followUpStatement);
                    } else if (this.currentFollowUpAction.equals("MBIA")) {
                        medicalBoardCaseSessionBeanLocal.createMedicalBoardCaseByBoard(selectedCase.getMedicalBoardCaseId(), MedicalBoardTypeEnum.ABSENCE, followUpStatement);
                    }
                }

                medicalBoardCaseSessionBeanLocal.signMedicalBoardCase(selectedCase.getMedicalBoardCaseId(), selectedCase.getBoardFindings(), selectedCase.getFinalPesStatus(), selectedCase.getConditionStatuses());
                medicalBoardSlot = slotSessionBeanLocal.retrieveMedicalBoardSlotById(medicalBoardSlot.getSlotId());
                medicalBoardSlot.getMedicalBoardCases().forEach(mbc -> {
                    if (mbc.equals(selectedCase)) {
                        selectedCase = mbc;
                    }
                });
                sortMedicalBoardCases(medicalBoardSlot.getMedicalBoardCases());
            } catch (SignMedicalBoardCaseException | CreateMedicalBoardCaseException ex) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to sign current case", ex.getMessage()));

            }
        }
    }

    public void addConditionStatus() {
        this.selectedCase.getConditionStatuses().add(new ConditionStatus());
    }

    public void doChangeDate(ConditionStatus conditionStatus, Integer duration) {

        if (duration != null) {

            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, duration);
            conditionStatus.setStatusEndDate(c.getTime());
        } else {
            conditionStatus.setStatusEndDate(null);
        }
    }

    public void copyCurrentConditionStatus() {

        List<ConditionStatus> copy = new ArrayList();

        for (ConditionStatus cs : this.servicemanCurrentConditionStatuses) {

            ConditionStatus newCs = new ConditionStatus();
            newCs.setDescription(cs.getDescription());
            newCs.setStatusEndDate(cs.getStatusEndDate());

            copy.add(newCs);

        }
        this.selectedCase.setConditionStatuses(copy);

    }

    public void toggleFollowUpAction() {
        this.hasFollowUp = !this.hasFollowUp;

        if (this.hasFollowUp) {
            followUpStatement = this.selectedCase.getStatementOfCase();
            currentFollowUpAction = "MBIP";
        }

    }

    public String getNewPreDefinedConditionStatus() {
        return newPreDefinedConditionStatus;
    }

    public void setNewPreDefinedConditionStatus(String newPreDefinedConditionStatus) {
        this.newPreDefinedConditionStatus = newPreDefinedConditionStatus;
    }

    public String getFollowUpStatement() {
        return followUpStatement;
    }

    public void setFollowUpStatement(String followUpStatement) {
        this.followUpStatement = followUpStatement;
    }

    public Boolean getHasFollowUp() {
        return hasFollowUp;
    }

    public void setHasFollowUp(Boolean hasFollowUp) {
        this.hasFollowUp = hasFollowUp;
    }

    public void clearEmptyConditionStatus() {
        boolean removed = true;
        while (removed) {
            removed = false;

            for (int x = 0; x < this.selectedCase.getConditionStatuses().size(); x++) {
                ConditionStatus condition = this.selectedCase.getConditionStatuses().get(x);

                if (condition.getDescription() == null) {
                    this.selectedCase.getConditionStatuses().remove(x);
                    removed = true;
                }
            }

        }

    }

    public void removeConditionStatus(ActionEvent event) {
        ConditionStatus cs = (ConditionStatus) event.getComponent().getAttributes().get("statusToDelete");
        for (int x = 0; x < this.selectedCase.getConditionStatuses().size(); x++) {
            ConditionStatus condition = this.selectedCase.getConditionStatuses().get(x);
            if (((condition.getDescription() == null && cs.getDescription() == null)
                    || condition.getDescription().equals(cs.getDescription()))
                    && ((condition.getStatusEndDate() == null && cs.getStatusEndDate() == null)
                    || condition.getStatusEndDate().equals(cs.getStatusEndDate()))) {
                this.selectedCase.getConditionStatuses().remove(x);
                break;
            }
        }
    }

    public void removePreDefinedConditionStatus(ActionEvent event) {
        PreDefinedConditionStatus cs = (PreDefinedConditionStatus) event.getComponent().getAttributes().get("statusToDelete");
        conditionStatusSessionBeanLocal.removePreDefinedConditionStatus(cs.getPreDefinedConditionStatusId());
        preDefinedConditionStatuses = conditionStatusSessionBeanLocal.retrieveAllPreDefinedConditionStatus();
    }

    public void addPreDefinedConditionStatus() {

        if (newPreDefinedConditionStatus != null && !newPreDefinedConditionStatus.isEmpty()) {
            conditionStatusSessionBeanLocal.addPreDefinedConditionStatus(newPreDefinedConditionStatus);
            newPreDefinedConditionStatus = "";
            preDefinedConditionStatuses = conditionStatusSessionBeanLocal.retrieveAllPreDefinedConditionStatus();

        }

    }

    public void rowSelectListener() {
        servicemanCurrentConditionStatuses = conditionStatusSessionBeanLocal.retrieveActiveConditionStatusByServiceman(this.selectedCase.getConsultation().getBooking().getServiceman().getServicemanId());
        this.followUpStatement = "";
        this.hasFollowUp = false;
    }

    public Boolean hasUnsignedCase() {

        if (medicalBoardSlot == null) {
            return false;
        }

        boolean result = false;

        for (MedicalBoardCase m : medicalBoardSlot.getMedicalBoardCases()) {
            if (!m.getIsSigned()) {
                result = true;
            }
        }

        sortMedicalBoardCases(medicalBoardSlot.getMedicalBoardCases());
        return result;

    }

    public void nextCase() {
        sortMedicalBoardCases(medicalBoardSlot.getMedicalBoardCases());
        if (medicalBoardSlot.getMedicalBoardCases().size() > 0) {
            hasFollowUp = false;
            selectedCase = medicalBoardSlot.getMedicalBoardCases().get(0);
            servicemanCurrentConditionStatuses = conditionStatusSessionBeanLocal.retrieveActiveConditionStatusByServiceman(this.selectedCase.getConsultation().getBooking().getServiceman().getServicemanId());
        }
    }

    public void endSession() {

        try {

            slotSessionBeanLocal.endMedicalBoardSession(medicalBoardSlot.getSlotId());
            FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board.xhtml");

        } catch (EndMedicalBoardSessionException | IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An Error Has Occured!", ex.getMessage()));

        }

    }

    public String backgroundStyle(MedicalBoardCase mbc) {

        if (mbc.equals(selectedCase)) {
            return "x";
        } else {
            return mbc.getIsSigned() ? "sign-true" : "sign-false";
        }

    }

    public List<String> autoComplete(String status) {

        String queryLowerCase = status.toLowerCase();
        return preDefinedConditionStatuses.stream()
                .map(s -> s.getDescription())
                .filter(s -> {
                    return s.toLowerCase().contains(queryLowerCase);
                })
                .collect(Collectors.toList());

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

    public List<ConditionStatus> getServicemanCurrentConditionStatuses() {
        return servicemanCurrentConditionStatuses;
    }

    public void setServicemanCurrentConditionStatuses(List<ConditionStatus> servicemanCurrentConditionStatuses) {
        this.servicemanCurrentConditionStatuses = servicemanCurrentConditionStatuses;
    }

    public List<PreDefinedConditionStatus> getPreDefinedConditionStatuses() {
        return preDefinedConditionStatuses;
    }

    public void setPreDefinedConditionStatuses(List<PreDefinedConditionStatus> preDefinedConditionStatuses) {
        this.preDefinedConditionStatuses = preDefinedConditionStatuses;
    }

    public PesStatusEnum[] getPesStatuses() {
        return PesStatusEnum.values();
    }

    public String getCurrentFollowUpAction() {
        return currentFollowUpAction;
    }

    public void setCurrentFollowUpAction(String currentFollowUpAction) {
        this.currentFollowUpAction = currentFollowUpAction;
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
