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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exceptions.CreateBookingException;
import util.exceptions.GenerateFormInstanceException;
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

            System.out.println("Create booking called");
            System.out.println("Serviceman Name: " + serviceman.getName());
            System.out.println("Consultation Purpose " + consultationPurpose.getConsultationPurposeName());
            System.out.println("Booking start time: " + bookingSlot.getStartDateTime());
            System.out.println("Booking end time: " + bookingSlot.getEndDateTime());
            System.out.println("Number of generated forms: " + consultationPurpose.getFormTemplates().size());
            System.out.println("Generated forms: " + consultationPurpose.getFormTemplates().toString());
            System.out.println("Medical Centre: " + bookingSlot.getMedicalCentre().getName());

            Booking newBooking = new Booking(serviceman, consultationPurpose, bookingSlot);
            
            bookingSlot.setBooking(newBooking);
            serviceman.getBookings().add(newBooking);
            consultationPurpose.getBookings().add(newBooking);
            
            em.persist(newBooking);
            em.flush();
            
            for (FormTemplate ft : consultationPurpose.getFormTemplates()) {
                try {
                    Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(serviceman.getServicemanId(), ft.getFormTemplateId());
                    System.out.println("Generate form instance: " + formInstanceId);
                    FormInstance fi = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
                    System.out.println("Instance Name: " + fi.getFormTemplateMapping().getFormTemplateName());
                    newBooking.getFormInstances().add(fi);
                    fi.setBooking(newBooking);

                } catch (GenerateFormInstanceException ex) {
                    System.out.println("Exception CAUGHT " + ex.getMessage());
                    continue;
                }
            }
            
            return newBooking;

        } catch (ServicemanNotFoundException ex) {
            throw new CreateBookingException("Serviceman Id not valid");
        }

    }
    
    @Override
    public List<Booking> retrieveServicemanBookings(Long servicemanId){
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.serviceman.servicemanId = :id ");
        query.setParameter("id", servicemanId);
        return query.getResultList();
    }

}
