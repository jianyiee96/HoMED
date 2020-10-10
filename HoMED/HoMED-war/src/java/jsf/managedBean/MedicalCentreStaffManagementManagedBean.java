/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import entity.MedicalCentre;
import entity.MedicalStaff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.AssignMedicalStaffToMedicalCentreException;
import util.exceptions.MedicalCentreNotFoundException;

@Named(value = "medicalCentreStaffManagementManagedBean")
@ViewScoped
public class MedicalCentreStaffManagementManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;
    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Long medicalCentreToViewId;
    private MedicalCentre medicalCentreToView;

    private List<MedicalStaff> unassignedMedicalStaff;

    public MedicalCentreStaffManagementManagedBean() {
        this.unassignedMedicalStaff = new ArrayList<>();

        try {
            this.medicalCentreToViewId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("medicalCentreToViewId"));
        } catch (NumberFormatException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Centre Staff Management", "Invalid Medical Centre ID"));
        }
    }

    @PostConstruct
    public void postConstruct() {
        if (this.medicalCentreToViewId != null) {
            refreshMedicalCentreToView(medicalCentreToViewId);
        }
    }

    private void refreshMedicalCentreToView(Long medicalCentreToViewId) {
        try {
            this.medicalCentreToView = this.medicalCentreSessionBeanLocal.retrieveMedicalCentreById(medicalCentreToViewId);
        } catch (MedicalCentreNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Centre Staff Management", ex.getMessage()));
        }
    }

    public void initAssignMedicalCentreStaff() {
        this.unassignedMedicalStaff = medicalCentreSessionBeanLocal.retrieveUnassignedMedicalStaffAndAssignedMedicalStaffByMedicalCentreId(medicalCentreToView);
    }

    public void assignMedicalStaffToMedicalCentre(MedicalStaff medicalStaff) {
        try {
            employeeSessionBeanLocal.assignMedicalStaffToMedicalCentre(medicalStaff.getEmployeeId(), medicalCentreToView.getMedicalCentreId());
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Centre Staff Management", "Successfully assigning medical staff!"));
            refreshMedicalCentreToView(medicalCentreToView.getMedicalCentreId());
            initAssignMedicalCentreStaff();
        } catch (AssignMedicalStaffToMedicalCentreException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Centre Staff Management", ex.getMessage()));

        }
    }

    public Long getMedicalCentreToViewId() {
        return medicalCentreToViewId;
    }

    public void setMedicalCentreToViewId(Long medicalCentreToViewId) {
        this.medicalCentreToViewId = medicalCentreToViewId;
    }

    public MedicalCentre getMedicalCentreToView() {
        return medicalCentreToView;
    }

    public void setMedicalCentreToView(MedicalCentre medicalCentreToView) {
        this.medicalCentreToView = medicalCentreToView;
    }

    public List<MedicalStaff> getUnassignedMedicalStaff() {
        return unassignedMedicalStaff;
    }

    public void setUnassignedMedicalStaff(List<MedicalStaff> unassignedMedicalStaff) {
        this.unassignedMedicalStaff = unassignedMedicalStaff;
    }

}
