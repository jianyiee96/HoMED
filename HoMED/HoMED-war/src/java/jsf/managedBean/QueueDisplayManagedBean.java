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
import javax.faces.application.FacesMessage;
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

    private HashSet<Long> changedBookingIdsHS;

    public QueueDisplayManagedBean() {
        currentBookings = new ArrayList<>();
        queueingBookings = new ArrayList<>();
        changedBookingIdsHS = new HashSet<>();
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
        List<Booking> holderCurrentBookings = new ArrayList<>();
        this.queueingBookings.clear();
        newBookings.forEach(b -> {
            if (b.getConsultation().getConsultationStatusEnum() == ConsultationStatusEnum.ONGOING) {
                holderCurrentBookings.add(b);
            } else {
                this.queueingBookings.add(b);
            }
        });

        // DETECTING CHANGES HERE
        this.changedBookingIdsHS.clear();
        Boolean detectChange = this.currentBookings.size() != holderCurrentBookings.size();
        if (!detectChange) {
            holderCurrentBookings.stream()
                    .map(b1 -> {
                        Boolean notFound = this.currentBookings.stream().noneMatch(b2 -> b1.equals(b2));
                        if (notFound) {
                            changedBookingIdsHS.add(b1.getBookingId());
                        }
                        return notFound;
                    })
                    .reduce(false, (b1, b2) -> b1 || b2);
        }
        System.out.println("DETECT CHANGE: " + detectChange);
        if (detectChange) {
            // @WK - Can help me figure out a way to trigger CSS here (Blinking Queue Number)
            this.currentBookings = holderCurrentBookings;
            // Update has to be called here so that IDs can be generated first
            PrimeFaces.current().ajax().update("formQueueDisplay:panel-all");
//          :panel-current-bookings
//          Component ID of specific card to blink: booking_#{booking.bookingId}
            //Trigger JS
//          PrimeFaces.current().executeScript("alert('peek-a-boo');");
        }
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

    public HashSet<Long> getChangedBookingIdsHS() {
        return changedBookingIdsHS;
    }

}
