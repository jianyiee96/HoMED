/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import java.util.List;
import ws.wrapper.MedicalBoardCaseWrapper;

/**
 *
 * @author rycan
 */
public class RetrieveAllServicemanMedicalBoardCasesRsp {
    
    private List<MedicalBoardCaseWrapper> medicalBoardCases;

    public RetrieveAllServicemanMedicalBoardCasesRsp() {
    }

    public RetrieveAllServicemanMedicalBoardCasesRsp(List<MedicalBoardCaseWrapper> medicalBoardCases) {
        this.medicalBoardCases = medicalBoardCases;
    }

    public List<MedicalBoardCaseWrapper> getMedicalBoardCases() {
        return medicalBoardCases;
    }

    public void setMedicalBoardCases(List<MedicalBoardCaseWrapper> medicalBoardCases) {
        this.medicalBoardCases = medicalBoardCases;
    }
    
}
