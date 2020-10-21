package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import entity.Booking;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalStaff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.ConsultationStatusEnum;

@Named(value = "queueDisplayManagedBean")
@ViewScoped
public class QueueDisplayManagedBean implements Serializable {

    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    private MedicalStaff currentMedicalStaff;
    private MedicalCentre currentMedicalCentre;

    private List<Booking> currentBookings;
    private List<Booking> queueingBookings;

    public QueueDisplayManagedBean() {
        currentBookings = new ArrayList<>();
        queueingBookings = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {

        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalStaff) {
            currentMedicalStaff = (MedicalStaff) currentEmployee;
            currentMedicalCentre = currentMedicalStaff.getMedicalCentre();
            refreshDisplay();
        }
    }

    public void refreshDisplay() {
        List<Booking> newBookings = bookingSessionBeanLocal.retrieveQueueBookingsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());

        this.currentBookings.clear();
        this.queueingBookings.clear();

        // To filter newly retrieved bookings into ONGOING and WAITING consultation bookings.
        newBookings.forEach(b -> {
            if (b.getConsultation().getConsultationStatusEnum() == ConsultationStatusEnum.ONGOING) {
                this.currentBookings.add(b);
            } else {
                this.queueingBookings.add(b);
            }
        });

    }

    public String formatQueueNumber(Long bookingId) {
        return String.format("%03d", bookingId % 1000);
    }

    public MedicalStaff getCurrentMedicalStaff() {
        return currentMedicalStaff;
    }

    public void setCurrentMedicalStaff(MedicalStaff currentMedicalStaff) {
        this.currentMedicalStaff = currentMedicalStaff;
    }

    public MedicalCentre getCurrentMedicalCentre() {
        return currentMedicalCentre;
    }

    public void setCurrentMedicalCentre(MedicalCentre currentMedicalCentre) {
        this.currentMedicalCentre = currentMedicalCentre;
    }

    public List<Booking> getCurrentBookings() {
        return currentBookings;
    }

    public List<Booking> getQueueingBookings() {
        return queueingBookings;
    }
}
