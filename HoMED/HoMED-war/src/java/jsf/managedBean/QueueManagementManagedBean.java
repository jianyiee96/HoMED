package jsf.managedBean;

import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Consultation;
import entity.Employee;
import entity.MedicalCentre;
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
import javax.faces.event.ActionEvent;
import util.exceptions.StartConsultationException;

@Named(value = "queueManagementManagedBean")
@ViewScoped
public class QueueManagementManagedBean implements Serializable {

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Consultation> waitingConsultations;

    private Consultation selectedConsultation;

    private MedicalOfficer currentMedicalOfficer;

    private MedicalCentre currentMedicalCentre;

    public QueueManagementManagedBean() {
        this.waitingConsultations = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(currentEmployee.getEmployeeId());
            currentMedicalCentre = currentMedicalOfficer.getMedicalCentre();
        }

        refreshConsultations();

        if (this.waitingConsultations.size() > 0) {
            this.selectedConsultation = this.waitingConsultations.get(0);
        }
    }

    public void refreshConsultations() {
        if (currentMedicalCentre != null && currentMedicalOfficer != null) {
            this.waitingConsultations = consultationSessionBeanLocal.retrieveWaitingConsultationsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());
        }
    }

    public void startSelectedConsultation() {

        try {
            consultationSessionBeanLocal.startConsultation(this.selectedConsultation.getConsultationId(), this.currentMedicalOfficer.getEmployeeId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Started consultation!", "You will be redirected to current consultation page"));

            FacesContext.getCurrentInstance().getExternalContext().redirect("current-consultation.xhtml");

        } catch (StartConsultationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to start consultation!", ex.getMessage()));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));

        }
    }

    public void selectConsultation(ActionEvent event) {
        this.selectedConsultation = (Consultation) event.getComponent().getAttributes().get("selectedConsultation");
    }

    public List<Consultation> getWaitingConsultations() {
        return waitingConsultations;
    }

    public void setWaitingConsultations(List<Consultation> waitingConsultations) {
        this.waitingConsultations = waitingConsultations;
    }

    public Consultation getSelectedConsultation() {
        return selectedConsultation;
    }

    public void setSelectedConsultation(Consultation selectedConsultation) {
        this.selectedConsultation = selectedConsultation;
    }

    public MedicalOfficer getCurrentMedicalOfficer() {
        return currentMedicalOfficer;
    }

    public void setCurrentMedicalOfficer(MedicalOfficer currentMedicalOfficer) {
        this.currentMedicalOfficer = currentMedicalOfficer;
    }

    public String formatQueueNumber(Long bookingId) {
        return String.format("%03d", bookingId % 1000);
    }

}
