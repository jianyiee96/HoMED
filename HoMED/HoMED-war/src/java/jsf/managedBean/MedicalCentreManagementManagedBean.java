/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import entity.MedicalCentre;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "medicalCentreManagementManagedBean")
@ViewScoped
public class MedicalCentreManagementManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private List<MedicalCentre> medicalCentres;

    private MedicalCentre selectedMedicalCentre;
    private Boolean isEditable;

    public MedicalCentreManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();
    }

    public void create() {
        isEditable = true;
        selectedMedicalCentre = new MedicalCentre();
    }

    public void view() {
        isEditable = false;
    }

    public void edit() {
        isEditable = true;
    }

    public void save() {
        if (selectedMedicalCentre.getMedicalCentreId() == null) {
            try {

                String address = selectedMedicalCentre.getStreetName() + "!!!@@!!!" 
                        + selectedMedicalCentre.getUnitNumber() + "!!!@@!!!" 
                        + selectedMedicalCentre.getBuildingName() + "!!!@@!!!" 
                        + selectedMedicalCentre.getCountry() + "!!!@@!!!" 
                        + selectedMedicalCentre.getPostal();
                
                selectedMedicalCentre.setAddress(address);

                Long medicalCentreId = medicalCentreSessionBeanLocal.createNewMedicalCentre(selectedMedicalCentre);

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New medical centre is created successfully!", null));

                selectedMedicalCentre = new MedicalCentre();
            } catch (InputDataValidationException | UnknownPersistenceException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new medical centre: " + ex.getMessage(), null));
            }
        }

//        try {
//            Long employeeId = employeeSessionBeanLocal.createNewEmployee(employeeToCreate);
//            employeeToCreate = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);
//
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New employee created successfully (Employee ID: " + employeeToCreate.getEmployeeId() + ")", null));
//
//            employees.add(employeeToCreate);
//            postConstruct();
//
//            employeeToCreate = new Employee();
//        } catch (EmployeeUsernameExistException | EmployeeNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new employee: " + ex.getMessage(), null));
//        }
    }

    public List<MedicalCentre> getMedicalCentres() {
        return medicalCentres;
    }

    public void setMedicalCentres(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }

    public MedicalCentre getSelectedMedicalCentre() {
        return selectedMedicalCentre;
    }

    public void setSelectedMedicalCentre(MedicalCentre selectedMedicalCentre) {
        this.selectedMedicalCentre = selectedMedicalCentre;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

}
