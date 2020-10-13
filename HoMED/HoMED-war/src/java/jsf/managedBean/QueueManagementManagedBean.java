/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ConsultationSessionBeanLocal;
import entity.Consultation;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalOfficer;
import entity.MedicalStaff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "queueManagementManagedBean")
@ViewScoped
public class QueueManagementManagedBean implements Serializable {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private List<Consultation> waitingConsultations;

    private MedicalStaff currentMedicalOfficer;

    private MedicalCentre currentMedicalCentre;

    public QueueManagementManagedBean() {
        this.waitingConsultations = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = (MedicalOfficer) currentEmployee;
            currentMedicalCentre = currentMedicalOfficer.getMedicalCentre();
            refreshConsultations();

        }

    }

    private void refreshConsultations() {

        if (currentMedicalCentre != null) {
            System.out.println("Kin");
            bookingSessionBeanLocal.retrieveAllBookings();
            this.waitingConsultations = consultationSessionBeanLocal.retrieveWaitingConsultationsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());
            System.out.println("Pin");
            System.out.println(this.waitingConsultations);
            System.out.println("Din");
        }

    }

    public String foo() {
        return "fool";
    }
}
