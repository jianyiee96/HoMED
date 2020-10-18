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
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "pastConsultationsManagedBean")
@ViewScoped
public class PastConsultationsManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private MedicalOfficer currentMedicalOfficer;
    private List<Consultation> pastConsultationsForCurrentMedicalOfficer;
    private Consultation selectedPastConsultation;

    public PastConsultationsManagedBean() {
        this.pastConsultationsForCurrentMedicalOfficer = new ArrayList<>();

    }

    @PostConstruct
    public void postConstruct() {
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(currentEmployee.getEmployeeId());
        }

        this.pastConsultationsForCurrentMedicalOfficer = consultationSessionBeanLocal.retrieveCompletedConsultationsByMedicalOfficerId(currentMedicalOfficer.getEmployeeId());
    }

    public MedicalOfficer getCurrentMedicalOfficer() {
        return currentMedicalOfficer;
    }

    public void setCurrentMedicalOfficer(MedicalOfficer currentMedicalOfficer) {
        this.currentMedicalOfficer = currentMedicalOfficer;
    }

    public List<Consultation> getPastConsultationsForCurrentMedicalOfficer() {
        return pastConsultationsForCurrentMedicalOfficer;
    }

    public void setPastConsultationsForCurrentMedicalOfficer(List<Consultation> pastConsultationsForCurrentMedicalOfficer) {
        this.pastConsultationsForCurrentMedicalOfficer = pastConsultationsForCurrentMedicalOfficer;
    }

    public Consultation getSelectedPastConsultation() {
        return selectedPastConsultation;
    }

    public void setSelectedPastConsultation(Consultation selectedPastConsultation) {
        this.selectedPastConsultation = selectedPastConsultation;
    }

}
