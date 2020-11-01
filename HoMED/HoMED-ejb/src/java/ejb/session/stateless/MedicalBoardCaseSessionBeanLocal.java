/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.MedicalBoardCase;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.MedicalBoardTypeEnum;
import util.exceptions.CreateMedicalBoardCaseException;

/**
 *
 * @author User
 */
@Local
public interface MedicalBoardCaseSessionBeanLocal {

    public void createMedicalBoardCaseByReview(Long consultationId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException;

    public void createMedicalBoardCaseByBoard(Long consultationId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException;

    public MedicalBoardCase retrieveMedicalBoardCaseById(Long medicalBoardCaseId);

    public List<MedicalBoardCase> retrieveUnassignedMedicalBoardInAbsenceCases();

    public List<MedicalBoardCase> retrieveUnassignedMedicalBoardInPresenceCases();
    
}
