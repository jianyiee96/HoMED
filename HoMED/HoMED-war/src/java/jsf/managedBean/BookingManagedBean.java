package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.BookingSlot;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalStaff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.EmployeeNotFoundException;

@Named(value = "bookingManagedBean")
@ViewScoped
public class BookingManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @EJB
    private SlotSessionBeanLocal slotSessionBean;

    private MedicalStaff currentMedicalStaff;

    private MedicalCentre currentMedicalCentre;

    private List<BookingSlot> bookingSlots;

    public BookingManagedBean() {
        bookingSlots = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null) {
            try {
                currentEmployee = employeeSessionBean.retrieveEmployeeById(currentEmployee.getEmployeeId());
                if (currentEmployee instanceof MedicalStaff) {
                    currentMedicalStaff = (MedicalStaff) currentEmployee;
                    currentMedicalCentre = currentMedicalStaff.getMedicalCentre();
                    if (currentMedicalCentre != null) {
                        bookingSlots = slotSessionBean.retrieveBookingSlotsWithBookingsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());
                    }
                }
            } catch (EmployeeNotFoundException ex) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bookings", ex.getMessage()));
            }
        }
    }
    
    public void deleteBooking() {
        
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

    public List<BookingSlot> getBookingSlots() {
        return bookingSlots;
    }

    public void setBookingSlots(List<BookingSlot> bookingSlots) {
        this.bookingSlots = bookingSlots;
    }

}
