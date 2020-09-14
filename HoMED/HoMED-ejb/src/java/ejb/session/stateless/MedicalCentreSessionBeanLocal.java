/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.MedicalCentre;
import entity.OperatingHours;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface MedicalCentreSessionBeanLocal {

    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws InputDataValidationException, UnknownPersistenceException;

    public Long createNewOperatingHours(OperatingHours newOperatingHours);
    
}
