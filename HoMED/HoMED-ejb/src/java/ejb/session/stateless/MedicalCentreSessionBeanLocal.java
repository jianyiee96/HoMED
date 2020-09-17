/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.MedicalCentre;
import entity.OperatingHours;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface MedicalCentreSessionBeanLocal {

    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws InputDataValidationException, UnknownPersistenceException;

    public Long createNewOperatingHours(OperatingHours newOperatingHours);

    public List<MedicalCentre> retrieveAllMedicalCentres();

    public MedicalCentre retrieveMedicalCentreById(Long medicalCentreId) throws MedicalCentreNotFoundException;

    public void updateMedicalCentre(MedicalCentre medicalCentre) throws MedicalCentreNotFoundException, InputDataValidationException;

    public void deleteMedicalCentre(Long medicalCentreId) throws MedicalCentreNotFoundException;
    
}
