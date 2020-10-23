package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import entity.Booking;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalStaff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
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

    private HashSet<Long> changedBookingsHS;

    public QueueDisplayManagedBean() {
        currentBookings = new ArrayList<>();
        queueingBookings = new ArrayList<>();
        changedBookingsHS = new HashSet<>();
    }

    @PostConstruct
    public void postConstruct() {

        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalStaff) {
            currentMedicalStaff = (MedicalStaff) currentEmployee;
            currentMedicalCentre = currentMedicalStaff.getMedicalCentre();
            if (currentMedicalCentre != null) {
                refreshDisplay();
            }
        }
    }

    public void refreshDisplay() {
        List<Booking> newBookings = bookingSessionBeanLocal.retrieveQueueBookingsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());
        List<Booking> holderCurrentBookings = new ArrayList<>();

//        this.currentBookings.clear();
        this.queueingBookings.clear();

        // To filter newly retrieved bookings into ONGOING and WAITING consultation bookings.
        newBookings.forEach(b -> {
            if (b.getConsultation().getConsultationStatusEnum() == ConsultationStatusEnum.ONGOING) {
                holderCurrentBookings.add(b);
            } else {
                this.queueingBookings.add(b);
            }
        });

        this.changedBookingsHS.clear();
        holderCurrentBookings.stream()
                .map(b1 -> {
                    Boolean notFound = this.currentBookings.stream().noneMatch(b2 -> b1.equals(b2));
                    if (notFound) {
                        changedBookingsHS.add(b1.getBookingId());
                    }
                    return notFound;
                })
                .reduce(false, (b1, b2) -> b1 || b2);
        this.currentBookings = holderCurrentBookings;
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

    public HashSet<Long> getChangedBookingsHS() {
        return changedBookingsHS;
    }

}
