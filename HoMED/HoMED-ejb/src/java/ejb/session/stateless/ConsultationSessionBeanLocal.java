/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import javax.ejb.Local;
import util.exceptions.CreateConsultationException;


@Local
public interface ConsultationSessionBeanLocal {
    
    public void createConsultationForBooking(Long bookingId) throws CreateConsultationException;
    
}
