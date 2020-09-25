package ejb.session.stateless;

import entity.Address;
import entity.MedicalCentre;
import entity.OperatingHours;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.CreateMedicalCentreException;
import util.exceptions.DeleteMedicalCentreException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UpdateMedicalCentreException;

@Stateless
public class MedicalCentreSessionBean implements MedicalCentreSessionBeanLocal {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public MedicalCentreSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws CreateMedicalCentreException {
        String errorMessage = "Failed to create Medical Centre: ";

        try {
            Set<ConstraintViolation<MedicalCentre>> constraintViolations = validator.validate(newMedicalCentre);

            if (constraintViolations.isEmpty()) {

                OperatingHours operatingHours = isOperatingHoursValid(newMedicalCentre.getOperatingHours());
                if (operatingHours != null) {
                    throw new CreateMedicalCentreException("[" + operatingHours.getDayOfWeek() + "] Opening Hours must be before Closing Hours");
                }

                for (OperatingHours oh : newMedicalCentre.getOperatingHours()) {
                    em.persist(oh);
                    em.flush();
                }

//                for (OperatingHours oh : newMedicalCentre.getOperatingHours()) {
//                    if (oh.getIsClose()) {
//                        em.persist(oh);
//                        em.flush();
//                    } else {
//                        if (oh.getOpeningHours() != null && oh.getClosingHours() != null) {
//                            if (oh.getClosingHours().isAfter(oh.getOpeningHours())) {
//                                em.persist(oh);
//                                em.flush();
//                            } else {
//                                throw new CreateMedicalCentreException("[" + oh.getDayOfWeek() + "] Opening Hours must be before Closing Hours");
//                            }
//                        } else {
//                            throw new CreateMedicalCentreException("[" + oh.getDayOfWeek() + "] Operating Hours is required");
//                        }
//                    }
//                }
                trimAddress(newMedicalCentre.getAddress());

                em.persist(newMedicalCentre);
                em.flush();

                return newMedicalCentre.getMedicalCentreId();
            } else {
                throw new CreateMedicalCentreException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (CreateMedicalCentreException ex) {
            throw new CreateMedicalCentreException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new CreateMedicalCentreException(generalUnexpectedErrorMessage + "creating Medical Centre [Persistence Exception]");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            ex.printStackTrace();
            throw new CreateMedicalCentreException(generalUnexpectedErrorMessage + "creating Medical Centre");
        }
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
    public void updateMedicalCentre(MedicalCentre medicalCentre) throws UpdateMedicalCentreException {
        String errorMessage = "Failed to update Medical Centre: ";
        try {
            if (medicalCentre != null && medicalCentre.getMedicalCentreId() != null) {
                Set<ConstraintViolation<MedicalCentre>> constraintViolations = validator.validate(medicalCentre);

                if (constraintViolations.isEmpty()) {
                    MedicalCentre medicalCentreToUpdate = retrieveMedicalCentreById(medicalCentre.getMedicalCentreId());

                    medicalCentreToUpdate.setName(medicalCentre.getName());
                    medicalCentreToUpdate.setPhone(medicalCentre.getPhone());
                    medicalCentreToUpdate.setAddress(medicalCentre.getAddress());
                    trimAddress(medicalCentreToUpdate.getAddress());

                    OperatingHours operatingHours = isOperatingHoursValid(medicalCentre.getOperatingHours());
                    if (operatingHours != null) {
                        throw new UpdateMedicalCentreException("[" + operatingHours.getDayOfWeek() + "] Opening Hours must be before Closing Hours");
                    }

                    List<OperatingHours> ohs = medicalCentre.getOperatingHours();
                    List<OperatingHours> ohsToUpdate = medicalCentreToUpdate.getOperatingHours();

                    for (int i = 0; i < ohsToUpdate.size(); i++) {
                        ohsToUpdate.get(i).setIsClose(ohs.get(i).getIsClose());
                        ohsToUpdate.get(i).setOpeningHours(ohs.get(i).getOpeningHours());
                        ohsToUpdate.get(i).setClosingHours(ohs.get(i).getClosingHours());
                    }
                } else {
                    throw new UpdateMedicalCentreException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            } else {
                throw new UpdateMedicalCentreException("Medical Centre information not found");
            }
        } catch (UpdateMedicalCentreException ex) {
            throw new UpdateMedicalCentreException(errorMessage + ex.getMessage());
        } catch (MedicalCentreNotFoundException ex) {
            throw new UpdateMedicalCentreException(errorMessage + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UpdateMedicalCentreException(generalUnexpectedErrorMessage + "updating medical centre");
        }
    }

    private Address trimAddress(Address medicalCentreAddress) {
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

        return medicalCentreAddress;
    }

    private OperatingHours isOperatingHoursValid(List<OperatingHours> operatingHours) {
        // To check if the operating hours provided are valid.
        for (OperatingHours oh : operatingHours) {
            if (!oh.getIsClose() && !oh.getClosingHours().isAfter(oh.getOpeningHours())) {
                return oh;
            }
        }

        return null;
    }

    @Override
    public void deleteMedicalCentre(Long medicalCentreId) throws DeleteMedicalCentreException {
        try {
            MedicalCentre medicalCentreToRemove = retrieveMedicalCentreById(medicalCentreId);

            for (OperatingHours ohs : medicalCentreToRemove.getOperatingHours()) {
                em.remove(ohs);
            }

            em.remove(medicalCentreToRemove);
        } catch (MedicalCentreNotFoundException ex) {
            throw new DeleteMedicalCentreException("Failed to delete Medical Centre: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeleteMedicalCentreException(generalUnexpectedErrorMessage + "deleting medical centre");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<MedicalCentre>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

}
