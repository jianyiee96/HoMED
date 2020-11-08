/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConditionStatus;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.MedicalBoardTypeEnum;
import util.enumeration.PesStatusEnum;
import util.exceptions.CreateMedicalBoardCaseException;
import util.exceptions.SignMedicalBoardCaseException;
import util.exceptions.UpdateMedicalBoardSlotException;

/**
 *
 * @author User
 */
@Local
public interface MedicalBoardCaseSessionBeanLocal {

    public void createMedicalBoardCaseByReview(Long consultationId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException;

    public void createMedicalBoardCaseByBoard(Long predecessorMedicalBoardCaseId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException;

    public void signMedicalBoardCase(Long medicalBoardCaseId, String boardFindings, PesStatusEnum newPesStatus, List<ConditionStatus> conditionStatuses) throws SignMedicalBoardCaseException;

    public MedicalBoardCase retrieveMedicalBoardCaseById(Long medicalBoardCaseId);

    public List<MedicalBoardCase> retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum medicalBoardTypeEnum, MedicalBoardSlot medicalBoardSlot);

    public List<MedicalBoardCase> retrieveUnallocatedMedicalBoardCases(MedicalBoardTypeEnum medicalBoardTypeEnum);

    public void allocateMedicalBoardCasesToMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot, List<MedicalBoardCase> medicalBoardCases) throws UpdateMedicalBoardSlotException;
//    public void allocateMedicalBoardCasesToMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot, List<Long> medicalBoardCaseIds) throws UpdateMedicalBoardSlotException;
    public List<MedicalBoardCase> retrieveAllMedicalBoardCases();
    
    public List<MedicalBoardCase> retrieveMedicalBoardCasesByServiceman(Long servicemanId);

}
