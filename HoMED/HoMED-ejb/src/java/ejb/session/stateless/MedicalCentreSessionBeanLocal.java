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
import util.exceptions.UnknownPersistenceException;

@Local
public interface MedicalCentreSessionBeanLocal {

    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws InputDataValidationException, UnknownPersistenceException;

    public Long createNewOperatingHours(OperatingHours newOperatingHours);

    public List<MedicalCentre> retrieveAllMedicalCentres();
    
}
