/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Consultation;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.MedicalBoardCaseStatusEnum;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.enumeration.PesStatusEnum;
import util.exceptions.CreateMedicalBoardCaseException;
import util.exceptions.SignMedicalBoardCaseException;
import util.exceptions.UpdateMedicalBoardSlotException;

@Stateless
public class MedicalBoardCaseSessionBean implements MedicalBoardCaseSessionBeanLocal {
    
    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;
    
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
    public void signMedicalBoardCase(Long medicalBoardCaseId, String boardFindings, PesStatusEnum newPesStatus) throws SignMedicalBoardCaseException {
        
        MedicalBoardCase medicalBoardCase = retrieveMedicalBoardCaseById(medicalBoardCaseId);
        
        if(medicalBoardCase == null) {
            throw new SignMedicalBoardCaseException("Invalid Medical Board Case Id");
        } else if (medicalBoardCase.getIsSigned()) {
            throw new SignMedicalBoardCaseException("Unable to sign medical board case: Case is signed already");
        } else if (boardFindings == null || boardFindings.isEmpty()) {
            throw new SignMedicalBoardCaseException("Unable to sign medical board case: Please include board findings.");
        }
        
        medicalBoardCase.setIsSigned(Boolean.TRUE);
        medicalBoardCase.setBoardFindings(boardFindings);
        
        
    }
    
    @Override
    public List<MedicalBoardCase> retrieveUnallocatedMedicalBoardCases(MedicalBoardTypeEnum medicalBoardTypeEnum) {
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardSlot IS NULL AND mbc.medicalBoardType = :boardType ORDER BY mbc.consultation.endDateTime ASC");
        query.setParameter("boardType", medicalBoardTypeEnum);
        
        return query.getResultList();
    }
    
    @Override
    public List<MedicalBoardCase> retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum medicalBoardTypeEnum, MedicalBoardSlot medicalBoardSlot) {
        
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardSlot = :boardSlot AND mbc.medicalBoardType = :boardType ORDER BY mbc.consultation.endDateTime ASC");
        query.setParameter("boardSlot", medicalBoardSlot);
        query.setParameter("boardType", medicalBoardTypeEnum);
        
        return query.getResultList();
    }
    
    @Override
    public MedicalBoardCase retrieveMedicalBoardCaseById(Long medicalBoardCaseId) {
        MedicalBoardCase medicalBoardCase = em.find(MedicalBoardCase.class, medicalBoardCaseId);
        return medicalBoardCase;
    }
    
    @Override
    public void allocateMedicalBoardCasesToMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot, List<MedicalBoardCase> medicalBoardCases) throws UpdateMedicalBoardSlotException {
        String errorMessage = "Failed to allocate Medical Board Cases to Medical Board Slot: ";
        
        if (medicalBoardSlot != null && medicalBoardSlot.getSlotId() != null) {
            MedicalBoardSlot medicalBoardSlotToUpdate = slotSessionBeanLocal.retrieveMedicalBoardSlotById(medicalBoardSlot.getSlotId());
            
            medicalBoardSlotToUpdate.setEstimatedTimeForEachBoardInPresenceCase(medicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase());
            medicalBoardSlotToUpdate.setEstimatedTimeForEachBoardInAbsenceCase(medicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase());

            // To reset all the relationship
            for (int i = 0; i < medicalBoardSlotToUpdate.getMedicalBoardCases().size(); i++) {
                medicalBoardSlotToUpdate.getMedicalBoardCases().get(i).setMedicalBoardSlot(null);
                medicalBoardSlotToUpdate.getMedicalBoardCases().get(i).setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum.WAITING);
            }
            medicalBoardSlotToUpdate.getMedicalBoardCases().clear();
            
            medicalBoardCases.forEach(mbc -> {
                MedicalBoardCase medicalBoardCaseToUpdate = retrieveMedicalBoardCaseById(mbc.getMedicalBoardCaseId());
                medicalBoardCaseToUpdate.setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum.SCHEDULED);
                
                medicalBoardCaseToUpdate.setMedicalBoardSlot(medicalBoardSlotToUpdate);
                medicalBoardSlotToUpdate.getMedicalBoardCases().add(medicalBoardCaseToUpdate);
            });
            
            if (medicalBoardSlotToUpdate.getMedicalBoardCases().isEmpty()) {
                medicalBoardSlotToUpdate.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.ASSIGNED);
            } else {
                medicalBoardSlotToUpdate.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.ALLOCATED);
            }
        } else {
            throw new UpdateMedicalBoardSlotException(errorMessage + "Medical Board Slot ID not found!");
        }
    }
}
