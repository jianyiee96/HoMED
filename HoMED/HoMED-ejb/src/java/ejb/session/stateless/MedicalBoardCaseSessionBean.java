/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Consultation;
import entity.MedicalBoardCase;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.MedicalBoardTypeEnum;
import util.exceptions.CreateMedicalBoardCaseException;

@Stateless
public class MedicalBoardCaseSessionBean implements MedicalBoardCaseSessionBeanLocal {

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public void createMedicalBoardCaseByReview(Long consultationId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException {

        Consultation consultation = consultationSessionBeanLocal.retrieveConsultationById(consultationId);

        if (consultation == null) {
            throw new CreateMedicalBoardCaseException("Invalid Consultation Id");
        } else if (!consultation.getBooking().getIsForReview()) {
            throw new CreateMedicalBoardCaseException("Invalid Consultation Type: Booking for consultation is not for Pre Medical Board Review");
        } else if (statementOfCase == null || statementOfCase.isEmpty()) {
            throw new CreateMedicalBoardCaseException("Unable to create Medical Board Case: Please supply a statement of case");
        } else if (consultation.getMedicalBoardCase() != null) {
            throw new CreateMedicalBoardCaseException("Unable to create Medical Board Case: Current consultation is already tied to an existing medical board case");
        }

        MedicalBoardCase medicalBoardCase = new MedicalBoardCase(consultation, medicalBoardType, statementOfCase);
        consultation.setMedicalBoardCase(medicalBoardCase);

        em.persist(medicalBoardCase);
        em.flush();

    }

    @Override
    public void createMedicalBoardCaseByBoard(Long predecessorMedicalBoardCaseId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException {

        MedicalBoardCase predecessorMedicalBoardCase = this.retrieveMedicalBoardCaseById(predecessorMedicalBoardCaseId);

    }

    @Override
    public List<MedicalBoardCase> retrieveUnassignedMedicalBoardInPresenceCases() {
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardSlot IS NULL AND mbc.medicalBoardType = :boardType ORDER BY mbc.consultation.endDateTime ASC");
        query.setParameter("boardType", MedicalBoardTypeEnum.PRESENCE);

        return query.getResultList();
    }

    @Override
    public List<MedicalBoardCase> retrieveUnassignedMedicalBoardInAbsenceCases() {
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardSlot IS NULL AND mbc.medicalBoardType = :boardType ORDER BY mbc.consultation.endDateTime ASC");
        query.setParameter("boardType", MedicalBoardTypeEnum.ABSENCE);

        return query.getResultList();
    }

    @Override
    public MedicalBoardCase retrieveMedicalBoardCaseById(Long medicalBoardCaseId) {
        MedicalBoardCase medicalBoardCase = em.find(MedicalBoardCase.class, medicalBoardCaseId);
        return medicalBoardCase;
    }

}
