/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Consultation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.ConsultationStatusEnum;

@Stateless
public class ConsultationSessionBean implements ConsultationSessionBeanLocal {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public List<Consultation> retrieveWaitingConsultationsByMedicalCentre(Long medicalCentreId) {

        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.booking.bookingSlot.medicalCentre.medicalCentreId = :id AND c.consultationStatusEnum = :status");
        query.setParameter("id", medicalCentreId);
        query.setParameter("status", ConsultationStatusEnum.WAITING);

        return query.getResultList();

    }

}
