package jsf.managedBean;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import entity.Employee;
import entity.MedicalCentre;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.CreateMedicalCentreException;
import util.exceptions.DeleteMedicalCentreException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.UpdateMedicalCentreException;

@Named(value = "manageMedicalCentreManagedBean")
@ViewScoped
public class ManageMedicalCentreManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private Employee currentEmployee;

    private MedicalCentre medicalCentreToView;

    private Boolean isManageState;
    private Boolean isCreateState;

    private Boolean isAdminView;
    private Boolean isEditMode;
    private Boolean isHideAdminPanel;

    public ManageMedicalCentreManagedBean() {
        this.medicalCentreToView = new MedicalCentre();
    }

    @PostConstruct
    public void postConstruct() {
        this.isHideAdminPanel = false;

        this.isManageState = false;
        this.isCreateState = false;
        this.isAdminView = false;
        Object objCurrentEmployee = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (objCurrentEmployee != null) {
            this.currentEmployee = (Employee) objCurrentEmployee;
            if (this.currentEmployee.getRole() == EmployeeRoleEnum.SUPER_USER) {
                this.isAdminView = true;
            }
        }
    }

    public void initCreate() {
        this.isHideAdminPanel = false;
        this.medicalCentreToView = new MedicalCentre();
        this.isManageState = false;
        this.isCreateState = true;
        this.isEditMode = true;
    }

    public void initManage() {
        this.isHideAdminPanel = false;
        this.isManageState = true;
        this.isCreateState = false;
        this.isEditMode = false;
    }

    public void doCreate() {
        try {
            Long medicalCentreId = medicalCentreSessionBeanLocal.createNewMedicalCentre(medicalCentreToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New medical centre [ID: " + medicalCentreId + "] is created!", null));
            this.isHideAdminPanel = true;
            this.isEditMode = false;
        } catch (CreateMedicalCentreException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doSave() {
        try {
            medicalCentreSessionBeanLocal.updateMedicalCentre(medicalCentreToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical centre [ID: " + medicalCentreToView.getMedicalCentreId() + "] is updated!", null));
            this.isEditMode = false;
        } catch (UpdateMedicalCentreException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }
    
//    public void doViewAssignedMedicalStaff() {
//        try {
//            
//        } catch () {
//            
//        }
//    }

    // Need to check if there is any association with consultations/employees in the future.
    public void doDelete() {
        try {
            medicalCentreSessionBeanLocal.deleteMedicalCentre(medicalCentreToView.getMedicalCentreId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical centre [ID: " + medicalCentreToView.getMedicalCentreId() + "] is deleted!", null));
            this.isHideAdminPanel = true;
            this.isEditMode = false;
        } catch (DeleteMedicalCentreException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doReset() {
        try {
            medicalCentreToView = medicalCentreSessionBeanLocal.retrieveMedicalCentreById(medicalCentreToView.getMedicalCentreId());
            this.isEditMode = false;
        } catch (MedicalCentreNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public MedicalCentre getMedicalCentreToView() {
        return medicalCentreToView;
    }

    public void setMedicalCentreToView(MedicalCentre medicalCentreToView) {
        this.medicalCentreToView = medicalCentreToView;
    }

    public Boolean getIsManageState() {
        return isManageState;
    }

    public void setIsManageState(Boolean isManageState) {
        this.isManageState = isManageState;
    }

    public Boolean getIsCreateState() {
        return isCreateState;
    }

    public void setIsCreateState(Boolean isCreateState) {
        this.isCreateState = isCreateState;
    }

    public Boolean getIsAdminView() {
        return isAdminView;
    }

    public void setIsAdminView(Boolean isAdminView) {
        this.isAdminView = isAdminView;
    }

    public Boolean getIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(Boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public Boolean getIsHideAdminPanel() {
        return isHideAdminPanel;
    }

    public void setIsHideAdminPanel(Boolean isHideAdminPanel) {
        this.isHideAdminPanel = isHideAdminPanel;
    }

}
