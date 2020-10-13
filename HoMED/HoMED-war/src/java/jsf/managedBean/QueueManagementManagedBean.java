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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import util.exceptions.MarkBookingAttendanceException;

@Named(value = "queueManagementManagedBean")
@ViewScoped
public class QueueManagementManagedBean implements Serializable {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private List<Consultation> waitingConsultations;

    private Consultation selectedConsultation;

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
        }
        refreshConsultations();
        
        try {
            bookingSessionBeanLocal.markBookingAttendance(25l);
        } catch (MarkBookingAttendanceException ex) {
            System.out.println("Test mark attandance error: " + ex.getMessage());
        }

    }

    private void refreshConsultations() {

        if (currentMedicalCentre != null) {

            bookingSessionBeanLocal.retrieveAllBookings();
            this.waitingConsultations = consultationSessionBeanLocal.retrieveWaitingConsultationsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());

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

    public String renderTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss");
        return dateFormat.format(date);
    }

}
