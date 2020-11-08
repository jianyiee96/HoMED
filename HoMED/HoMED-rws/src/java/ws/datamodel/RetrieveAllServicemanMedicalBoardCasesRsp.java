/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.MedicalBoardCase;
import java.util.List;

/**
 *
 * @author rycan
 */
public class RetrieveAllServicemanMedicalBoardCasesRsp {
    
    private List<MedicalBoardCase> medicalBoardCases;

    public RetrieveAllServicemanMedicalBoardCasesRsp() {
    }

    public RetrieveAllServicemanMedicalBoardCasesRsp(List<MedicalBoardCase> medicalBoardCases) {
        this.medicalBoardCases = medicalBoardCases;
    }

    public List<MedicalBoardCase> getMedicalBoardCases() {
        return medicalBoardCases;
    }

    public void setMedicalBoardCases(List<MedicalBoardCase> medicalBoardCases) {
        this.medicalBoardCases = medicalBoardCases;
    }
    
}
