/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Address;
import entity.MedicalCentre;
import entity.OperatingHours;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.InputDataValidationException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class MedicalCentreSessionBean implements MedicalCentreSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    public MedicalCentreSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws InputDataValidationException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<MedicalCentre>> constraintViolations = validator.validate(newMedicalCentre);

            if (constraintViolations.isEmpty()) {

                for (OperatingHours oh : newMedicalCentre.getOperatingHours()) {
                    em.persist(oh);
                    em.flush();
                }

                Address medicalCentreAddress = newMedicalCentre.getAddress();
                if (medicalCentreAddress.getStreetName() != null) {
                    medicalCentreAddress.setStreetName(medicalCentreAddress.getStreetName().trim());
                }
                if (medicalCentreAddress.getUnitNumber() != null) {
                    medicalCentreAddress.setUnitNumber(medicalCentreAddress.getUnitNumber().trim());
                }
                if (medicalCentreAddress.getBuildingName() != null) {
                    medicalCentreAddress.setBuildingName(medicalCentreAddress.getBuildingName().trim());
                }
                if (medicalCentreAddress.getCountry() != null) {
                    medicalCentreAddress.setCountry(medicalCentreAddress.getCountry().trim());
                }
                if (medicalCentreAddress.getPostal() != null) {
                    medicalCentreAddress.setPostal(medicalCentreAddress.getPostal().trim());
                }

                em.persist(newMedicalCentre);
                em.flush();

                return newMedicalCentre.getMedicalCentreId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Long createNewOperatingHours(OperatingHours newOperatingHours) {
        em.persist(newOperatingHours);
        em.flush();

        return newOperatingHours.getOperatingHoursId();
    }

    @Override
    public List<MedicalCentre> retrieveAllMedicalCentres() {
        Query query = em.createQuery("SELECT mc FROM MedicalCentre mc ORDER BY mc.name ASC");
        List<MedicalCentre> medicalCentres = query.getResultList();

        for (MedicalCentre mc : medicalCentres) {
            mc.getOperatingHours().size();
        }

        return medicalCentres;
    }

    @Override
    public MedicalCentre retrieveMedicalCentreById(Long medicalCentreId) throws MedicalCentreNotFoundException {
        MedicalCentre medicalCentre = em.find(MedicalCentre.class, medicalCentreId);

        if (medicalCentre != null) {
            medicalCentre.getOperatingHours().size();
            return medicalCentre;
        } else {
            throw new MedicalCentreNotFoundException("Medical Centre ID " + medicalCentreId + " does not exist!");

        }
    }

    @Override
    public void updateMedicalCentre(MedicalCentre medicalCentre) throws MedicalCentreNotFoundException, InputDataValidationException {
        if (medicalCentre != null && medicalCentre.getMedicalCentreId() != null) {
            Set<ConstraintViolation<MedicalCentre>> constraintViolations = validator.validate(medicalCentre);

            if (constraintViolations.isEmpty()) {
                MedicalCentre medicalCentreToUpdate = retrieveMedicalCentreById(medicalCentre.getMedicalCentreId());

                medicalCentreToUpdate.setName(medicalCentre.getName());
                medicalCentreToUpdate.setPhone(medicalCentre.getPhone());
                medicalCentreToUpdate.setAddress(medicalCentre.getAddress());

                Address medicalCentreAddress = medicalCentreToUpdate.getAddress();
                if (medicalCentreAddress.getStreetName() != null) {
                    medicalCentreAddress.setStreetName(medicalCentreAddress.getStreetName().trim());
                }
                if (medicalCentreAddress.getUnitNumber() != null) {
                    medicalCentreAddress.setUnitNumber(medicalCentreAddress.getUnitNumber().trim());
                }
                if (medicalCentreAddress.getBuildingName() != null) {
                    medicalCentreAddress.setBuildingName(medicalCentreAddress.getBuildingName().trim());
                }
                if (medicalCentreAddress.getCountry() != null) {
                    medicalCentreAddress.setCountry(medicalCentreAddress.getCountry().trim());
                }
                if (medicalCentreAddress.getPostal() != null) {
                    medicalCentreAddress.setPostal(medicalCentreAddress.getPostal().trim());
                }

                List<OperatingHours> ohs = medicalCentre.getOperatingHours();
                List<OperatingHours> ohsToUpdate = medicalCentreToUpdate.getOperatingHours();
                for (int i = 0; i < ohsToUpdate.size(); i++) {
                    ohsToUpdate.get(i).setOpeningHours(ohs.get(i).getOpeningHours());
                    ohsToUpdate.get(i).setClosingHours(ohs.get(i).getClosingHours());
                }

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new MedicalCentreNotFoundException("Medical Centre ID is not provided to update!");
        }
    }

    @Override
    public void deleteMedicalCentre(Long medicalCentreId) throws MedicalCentreNotFoundException {
        MedicalCentre medicalCentreToRemove = retrieveMedicalCentreById(medicalCentreId);

        for (OperatingHours ohs : medicalCentreToRemove.getOperatingHours()) {
            em.remove(ohs);
        }

        em.remove(medicalCentreToRemove);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<MedicalCentre>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}