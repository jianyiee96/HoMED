/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.BookingStatusEnum;
import util.exceptions.CreateConsultationException;
import util.exceptions.EndConsultationException;
import util.exceptions.InvalidateConsultationException;
import util.exceptions.StartConsultationException;

@Stateless
public class ConsultationSessionBean implements ConsultationSessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public void createConsultationForBooking(Long bookingId) throws CreateConsultationException {

        Booking booking = bookingSessionBeanLocal.retrieveBookingById(bookingId);

        if (booking != null && booking.getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
            throw new CreateConsultationException("Not implemented");

            
            
        } else {
            throw new CreateConsultationException("Invalid Booking Id Supplied: Ensure booking id is for booking in upcoming status");
        }
        
    }
    
    public void startConsultation(Long consultationId, Long medicalOfficerId) throws StartConsultationException {
        
    }
    
    
    public void endConsultation(Long medicalOfficerId) throws EndConsultationException {
        
    }
    
    public void invalidateConsultation(Long medicalOfficerId) throws InvalidateConsultationException {
        
    }

}
