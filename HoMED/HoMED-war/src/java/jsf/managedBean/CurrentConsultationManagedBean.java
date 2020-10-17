/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Consultation;
import entity.Employee;
import entity.MedicalOfficer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import util.exceptions.EndConsultationException;
import util.exceptions.InvalidateConsultationException;

@Named(value = "currentConsultationManagedBean")
@ViewScoped
public class CurrentConsultationManagedBean implements Serializable {

    @Inject
    private ManageFormInstanceManagedBean manageFormInstanceManagedBean;

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Consultation> servicemanConsultations;

    private MedicalOfficer currentMedicalOfficer;

    private Consultation selectedConsultation;

    private int countdownRemainingSeconds;

    private String consultationNotes;

    private String remarksForServiceman;

    public CurrentConsultationManagedBean() {
        this.servicemanConsultations = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        countdownRemainingSeconds = 5;
        consultationNotes = "";
        remarksForServiceman = "";

        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(currentEmployee.getEmployeeId());
            selectedConsultation = currentMedicalOfficer.getCurrentConsultation();
        }

        refreshServicemanConsultations();

    }

    private void refreshServicemanConsultations() {

        if (currentMedicalOfficer != null && currentMedicalOfficer.getCurrentConsultation() != null) {

            this.servicemanConsultations = consultationSessionBeanLocal.retrieveServicemanNonWaitingConsultation(currentMedicalOfficer.getCurrentConsultation().getBooking().getServiceman().getServicemanId());

        }

    }

    public void onRowSelect(SelectEvent<Consultation> event) {
        selectedConsultation = (Consultation) event.getObject();
    }

    public void dialogActionListener() {
        this.selectedConsultation = consultationSessionBeanLocal.retrieveConsultationById(selectedConsultation.getConsultationId());
        refreshServicemanConsultations();
        if (manageFormInstanceManagedBean.getIsReloadMainPage()) {
            PrimeFaces.current().ajax().update("consultationForm:panelGroupForms");
        }
        if (manageFormInstanceManagedBean.getIsSuccessfulSubmit()) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Form Instance Submission", "Successfully submitted " + manageFormInstanceManagedBean.getFormInstanceToView().toString()));
        }
        if (manageFormInstanceManagedBean.getIsSuccessfulSave()) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Form Instance Saved", "Successfully saved form instance" + manageFormInstanceManagedBean.getFormInstanceToView().toString()));
        }
    }

    public void endCurrentConsultation() {

        try {
            Boolean unsignedForms = selectedConsultation.getBooking().getFormInstances().stream().anyMatch(fi -> fi.getSignedBy() == null);
            if (unsignedForms) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Form Instance Unsigned", "Please make sure all forms have been signed"));
            } else {
                consultationSessionBeanLocal.endConsultation(selectedConsultation.getConsultationId(), this.consultationNotes, this.remarksForServiceman);
                this.consultationNotes = "";
                this.remarksForServiceman = "";
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("queue-management.xhtml");
                } catch (IOException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
                }
            }
        } catch (EndConsultationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to end consultation!", ex.getMessage()));
        }
    }

    public void marknoShowCurrentConsultation() {
        try {
            consultationSessionBeanLocal.invalidateConsultation(selectedConsultation.getConsultationId());
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("queue-management.xhtml");
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
            }
        } catch (InvalidateConsultationException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mark No Show Error", ex.getMessage()));
        }
    }

    public Boolean getIsCurrentConsultation() {
        return selectedConsultation.equals(currentMedicalOfficer.getCurrentConsultation());
    }

    public List<Consultation> getServicemanConsultations() {
        return servicemanConsultations;
    }

    public void setServicemanConsultations(List<Consultation> servicemanConsultations) {
        this.servicemanConsultations = servicemanConsultations;
    }

    public MedicalOfficer getCurrentMedicalOfficer() {
        return currentMedicalOfficer;
    }

    public void setCurrentMedicalOfficer(MedicalOfficer currentMedicalOfficer) {
        this.currentMedicalOfficer = currentMedicalOfficer;
    }

    public Consultation getSelectedConsultation() {
        return selectedConsultation;
    }

    public void setSelectedConsultation(Consultation selectedConsultation) {
        this.selectedConsultation = selectedConsultation;
    }

    public ManageFormInstanceManagedBean getManageFormInstanceManagedBean() {
        return manageFormInstanceManagedBean;
    }

    public int getCountdownRemainingSeconds() {
        return countdownRemainingSeconds;
    }

    public String getCountdownValue() {
        int mins = countdownRemainingSeconds / 60;
        int remainder = countdownRemainingSeconds - (mins * 60);
        return String.format("%02d:%02d", mins, remainder);
    }

    public void decreaseCountdown() {
        this.countdownRemainingSeconds -= 1;
        if (this.countdownRemainingSeconds == 0) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("queue-management.xhtml");
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
            }
        }
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    public String getRemarksForServiceman() {
        return remarksForServiceman;
    }

    public void setRemarksForServiceman(String remarksForServiceman) {
        this.remarksForServiceman = remarksForServiceman;
    }

}