/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.MedicalCentre;
import entity.MedicalStaff;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.AssignMedicalStaffToMedicalCentreException;
import util.exceptions.CreateMedicalCentreException;
import util.exceptions.DeleteMedicalCentreException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UpdateMedicalCentreException;

@Local
public interface MedicalCentreSessionBeanLocal {

    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws CreateMedicalCentreException;

    public List<MedicalCentre> retrieveAllMedicalCentres();

    public MedicalCentre retrieveMedicalCentreById(Long medicalCentreId) throws MedicalCentreNotFoundException;

    public void updateMedicalCentre(MedicalCentre medicalCentre) throws UpdateMedicalCentreException;

    public void deleteMedicalCentre(Long medicalCentreId) throws DeleteMedicalCentreException;

    public void assignListOfMedicalStaffToMedicalCentre(Long medicalCentreId, List<MedicalStaff> medicalStaffList) throws AssignMedicalStaffToMedicalCentreException;

    public List<MedicalStaff> retrieveUnassignedMedicalStaffAndAssignedMedicalStaffByMedicalCentreId(MedicalCentre medicalCentre);

}
