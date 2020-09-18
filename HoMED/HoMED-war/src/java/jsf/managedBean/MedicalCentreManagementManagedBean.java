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
import org.primefaces.PrimeFaces;
import util.exceptions.InputDataValidationException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "medicalCentreManagementManagedBean")
@ViewScoped
public class MedicalCentreManagementManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private List<MedicalCentre> medicalCentres;

    private MedicalCentre medicalCentreToCreate;
    private MedicalCentre medicalCentreToManage;
    private MedicalCentre medicalCentreToDelete;

    private Boolean isEditable;

    public MedicalCentreManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();
        medicalCentreToCreate = new MedicalCentre();
    }

    public void create() {
        try {
            Long medicalCentreId = medicalCentreSessionBeanLocal.createNewMedicalCentre(medicalCentreToCreate);

            medicalCentres.add(medicalCentreToCreate);

            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Centre", "New medical centre [ID: " + medicalCentreId + "] is created!"));
            PrimeFaces.current().executeScript("PF('dialogCreateNewMedicalCentre').hide()");

            medicalCentreToCreate = new MedicalCentre();
        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new medical centre: " + ex.getMessage(), null));
        }
    }

    public void view(MedicalCentre medicalCentre) {
        isEditable = false;
        medicalCentreToManage = medicalCentre;
        this.medicalCentreToCreate = new MedicalCentre();
        this.medicalCentreToDelete = new MedicalCentre();
    }

    public void edit() {
        isEditable = true;
    }

    public void saveEdit() {
        try {
            isEditable = false;
            medicalCentreSessionBeanLocal.updateMedicalCentre(medicalCentreToManage);

            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Centre", "Medical centre [ID: " + medicalCentreToManage.getMedicalCentreId() + "] is updated!"));
            PrimeFaces.current().executeScript("PF('dialogManageMedicalCentre').hide()");

            medicalCentreToManage = new MedicalCentre();
        } catch (MedicalCentreNotFoundException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating the medical centre: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    // Need to check if there is any association with consultations/employees in the future
    public void delete() {
        try {
            medicalCentreSessionBeanLocal.deleteMedicalCentre(medicalCentreToDelete.getMedicalCentreId());
            medicalCentres.remove(medicalCentreToDelete);

            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Centre", "Medical centre [ID: " + medicalCentreToDelete.getMedicalCentreId() + "] is deleted!"));

            medicalCentreToDelete = new MedicalCentre();
        } catch (MedicalCentreNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting the medical centre: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public List<MedicalCentre> getMedicalCentres() {
        return medicalCentres;
    }

    public void setMedicalCentres(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }

    public MedicalCentre getMedicalCentreToCreate() {
        return medicalCentreToCreate;
    }

    public void setMedicalCentreToCreate(MedicalCentre medicalCentreToCreate) {
        this.medicalCentreToCreate = medicalCentreToCreate;
    }

    public MedicalCentre getMedicalCentreToManage() {
        return medicalCentreToManage;
    }

    public void setMedicalCentreToManage(MedicalCentre medicalCentreToManage) {
        this.medicalCentreToManage = medicalCentreToManage;
    }

    public MedicalCentre getMedicalCentreToDelete() {
        return medicalCentreToDelete;
    }

    public void setMedicalCentreToDelete(MedicalCentre medicalCentreToDelete) {
        this.medicalCentreToDelete = medicalCentreToDelete;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

}
