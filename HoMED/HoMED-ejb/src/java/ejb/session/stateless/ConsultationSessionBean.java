/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Consultation;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.ConsultationStatusEnum;
import util.exceptions.CreateConsultationException;

@Stateless
public class ConsultationSessionBean implements ConsultationSessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;
    
    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public void createConsultation(Long bookingId) throws CreateConsultationException {

        Booking booking = bookingSessionBeanLocal.retrieveBookingById(bookingId);
        
        if(booking == null) {
            throw new CreateConsultationException("Invalid Booking Id");
        }
        
        Consultation newConsultation = new Consultation();
        newConsultation.setBooking(booking);
        booking.setConsultation(newConsultation);
        
        em.persist(newConsultation);
        em.flush();
        
    }

    @Override
    public List<Consultation> retrieveWaitingConsultationsByMedicalCentre(Long medicalCentreId) {

        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.booking.bookingSlot.medicalCentre.medicalCentreId = :id AND c.consultationStatusEnum = :status");
        query.setParameter("id", medicalCentreId);
        query.setParameter("status", ConsultationStatusEnum.WAITING);

        return query.getResultList();

    }

}
