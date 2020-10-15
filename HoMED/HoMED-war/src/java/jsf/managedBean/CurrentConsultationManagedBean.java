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
import org.primefaces.event.SelectEvent;
import util.exceptions.EndConsultationException;

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

    public CurrentConsultationManagedBean() {
        this.servicemanConsultations = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
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

    public void endCurrentConsultation() {

        try {
            consultationSessionBeanLocal.endConsultation(selectedConsultation.getConsultationId(), selectedConsultation.getRemarks(), selectedConsultation.getRemarksForServiceman());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ended consultation!", "To-do: implement redirect?"));

        } catch (EndConsultationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to end consultation!", ex.getMessage()));
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

}
