/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.MedicalCentre;
import java.util.List;

public class RetrieveAllMedicalCentreRsp {
    
    List<MedicalCentre> medicalCentres;

    public RetrieveAllMedicalCentreRsp() {
    }

    public RetrieveAllMedicalCentreRsp(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }

    public List<MedicalCentre> getMedicalCentres() {
        return medicalCentres;
    }

    public void setMedicalCentres(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }
    
}
