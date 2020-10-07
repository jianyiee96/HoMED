package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Employee;
import entity.Serviceman;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.CreateServicemanException;
import util.exceptions.DeleteServicemanException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.UpdateServicemanException;

@Named(value = "manageServicemanManagedBean")
@ViewScoped
public class ManageServicemanManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private Serviceman servicemanToView;

    private Boolean isCreateState;

    private Employee employeeToCopy;

    private Boolean isAdminView;
    private Boolean isEditMode;
    private Boolean isHideAdminPanel;

    public ManageServicemanManagedBean() {
        this.servicemanToView = new Serviceman();
    }

    @PostConstruct
    public void postConstruct() {
        init();
        this.isCreateState = false;
        this.isAdminView = false;
        this.employeeToCopy = null;
        Object objCurrentEmployee = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (objCurrentEmployee != null) {
            Employee currentEmployee = (Employee) objCurrentEmployee;
            if (currentEmployee.getRole() == EmployeeRoleEnum.SUPER_USER) {
                this.isAdminView = true;
            }
        }
    }

    public void init() {
        this.isHideAdminPanel = false;
        this.employeeToCopy = null;
    }

    public void initCreate() {
        init();
        this.servicemanToView = new Serviceman();
        this.isCreateState = true;
        this.isEditMode = true;
    }

    public void initManage() {
        init();
        this.isCreateState = false;
        this.isEditMode = false;
    }

    public void doCreate() {
        try {
            servicemanSessionBeanLocal.createServiceman(servicemanToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created serviceman! Please inform serviceman that OTP has been sent to his/her email.", null));
            this.isHideAdminPanel = true;
            this.isEditMode = false;
        } catch (CreateServicemanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doSave() {
        try {
            servicemanSessionBeanLocal.updateServiceman(servicemanToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated serviceman!", null));
            this.isEditMode = false;
        } catch (UpdateServicemanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doResetPassword() {
        try {
            this.servicemanToView.setIsActivated(false);

            servicemanSessionBeanLocal.resetServicemanPasswordBySuperUser(servicemanToView);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully reset serviceman's password! Please inform serviceman that OTP has been sent to their email.", null));
        } catch (ResetServicemanPasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doDelete() {
        try {
            servicemanSessionBeanLocal.deleteServiceman(this.servicemanToView.getServicemanId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully deleted serviceman!", null));
            this.isHideAdminPanel = true;
            this.isEditMode = false;
        } catch (DeleteServicemanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void emailValidation() {
        try {
            this.employeeToCopy = employeeSessionBeanLocal.retrieveEmployeeByEmail(this.servicemanToView.getEmail());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee with same email detected. All details will also be updated on employee account.", null));
        } catch (EmployeeNotFoundException ex) {
            this.employeeToCopy = null;
        }
    }

    public void doCopyDetails() {
        this.servicemanToView.setName(employeeToCopy.getName());
        this.servicemanToView.setGender(employeeToCopy.getGender());
        this.servicemanToView.setPhoneNumber(employeeToCopy.getPhoneNumber());
        this.servicemanToView.setAddress(employeeToCopy.getAddress());
    }

    public Serviceman getServicemanToView() {
        return servicemanToView;
    }

    public void setServicemanToView(Serviceman servicemanToView) {
        this.servicemanToView = servicemanToView;
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

    public Boolean getIsCreateState() {
        return isCreateState;
    }

    public void setIsCreateState(Boolean isCreateState) {
        this.isCreateState = isCreateState;
    }

    public Employee getEmployeeToCopy() {
        return employeeToCopy;
    }

    public void setEmployeeToCopy(Employee employeeToCopy) {
        this.employeeToCopy = employeeToCopy;
    }

}
