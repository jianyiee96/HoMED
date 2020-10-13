/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import entity.BookingSlot;
import entity.ConsultationPurpose;
import entity.FormInstance;
import entity.FormTemplate;
import entity.Serviceman;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.BookingStatusEnum;
import util.exceptions.CancelBookingException;
import util.exceptions.CreateBookingException;
import util.exceptions.DeleteFormInstanceException;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ServicemanNotFoundException;

@Stateless
public class BookingSessionBean implements BookingSessionBeanLocal {

    @EJB
    private SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public Booking createBooking(Long servicemanId, Long consultationPurposeId, Long bookingSlotId) throws CreateBookingException {

        try {

            Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(servicemanId);
            ConsultationPurpose consultationPurpose = consultationPurposeSessionBeanLocal.retrieveConsultationPurpose(consultationPurposeId);
            BookingSlot bookingSlot = slotSessionBeanLocal.retrieveBookingSlotById(bookingSlotId);

            if (consultationPurpose == null) {
                throw new CreateBookingException("Consultation Purpose Id not valid");
            } else if (bookingSlot == null) {
                throw new CreateBookingException("Booking Slot Id not valid");
            } else if (bookingSlot.getBooking() != null) {
                throw new CreateBookingException("Booking Slot not valid: Has existing booking");
            } else if (bookingSlot.getStartDateTime().before(new Date())) {
                throw new CreateBookingException("Booking Slot not valid: Invalid Start Date");
            }

            Booking newBooking = new Booking(serviceman, consultationPurpose, bookingSlot);

            bookingSlot.setBooking(newBooking);
            serviceman.getBookings().add(newBooking);
            consultationPurpose.getBookings().add(newBooking);

            em.persist(newBooking);
            em.flush();

            // Generate and form templates that are linked to cosultation purpose
            for (FormTemplate ft : consultationPurpose.getFormTemplates()) {
                try {
                    Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(serviceman.getServicemanId(), ft.getFormTemplateId());
                    FormInstance fi = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
                    newBooking.getFormInstances().add(fi);
                    fi.setBooking(newBooking);

                } catch (GenerateFormInstanceException ex) {
                    continue;
                }
            }

            // Notification module can fire here
            return newBooking;

        } catch (ServicemanNotFoundException ex) {
            throw new CreateBookingException("Serviceman Id not valid");
        }

    }

    @Override
    public List<Booking> retrieveServicemanBookings(Long servicemanId) {
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.serviceman.servicemanId = :id ");
        query.setParameter("id", servicemanId);
        return query.getResultList();
    }

    @Override
    public Booking retrieveBookingById(Long bookingId) {
        Booking booking = em.find(Booking.class, bookingId);
        return booking;
    }

    @Override
    public void cancelBooking(Long bookingId) throws CancelBookingException {

        if (bookingId == null) {
            throw new CancelBookingException("Please supply a valid Booking Id");
        }

        Booking booking = retrieveBookingById(bookingId);

        if (booking != null) {

            if (booking.getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {

                try {
                    booking.setBookingStatusEnum(BookingStatusEnum.CANCELLED);

                    for(FormInstance fi : booking.getFormInstances()) {
                        
                        formInstanceSessionBeanLocal.deleteFormInstance(fi.getFormInstanceId(), Boolean.TRUE);
                        
                    }
                    
                    slotSessionBeanLocal.createBookingSlots(
                            booking.getBookingSlot().getMedicalCentre().getMedicalCentreId(),
                            booking.getBookingSlot().getStartDateTime(),
                            booking.getBookingSlot().getEndDateTime());

                } catch (ScheduleBookingSlotException | DeleteFormInstanceException ex) {
                    throw new CancelBookingException("Unable to create replacement booking slot: " + ex.getMessage());

                }

            } else {
                throw new CancelBookingException("Invalid Booking Status: You can only cancel bookings with upcoming");
            }

        } else {
            throw new CancelBookingException("Invalid Booking Id");
        }

    }

}
