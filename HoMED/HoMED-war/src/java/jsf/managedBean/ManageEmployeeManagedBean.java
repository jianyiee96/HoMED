package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.DeleteEmployeeException;
import util.exceptions.ResetEmployeePasswordException;
import util.exceptions.UpdateEmployeeException;

@Named(value = "manageEmployeeManagedBean")
@ViewScoped
public class ManageEmployeeManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    private Employee currentEmployee;

    private Employee employeeToView;

    private Boolean isAdminView;
    private Boolean isEditMode;
    private Boolean isDeleted;

    public ManageEmployeeManagedBean() {
        this.employeeToView = new Employee();
    }

    @PostConstruct
    public void postConstruct() {
        init();
        this.isAdminView = false;
        Object objCurrentEmployee = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (objCurrentEmployee != null) {
            this.currentEmployee = (Employee) objCurrentEmployee;
            if (this.currentEmployee.getRole() == EmployeeRoleEnum.ADMIN) {
                this.isAdminView = true;
            }
        }
    }

    public void init() {
        this.isEditMode = false;
        this.isDeleted = false;
    }

    public void doEdit() {
        this.isEditMode = true;
    }

    public void doSave() {
        try {
            employeeSessionBean.updateEmployee(employeeToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated employee!", null));
            this.isEditMode = false;
        } catch (UpdateEmployeeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doResetPassword() {
        try {
            // isActivated is changed
            this.employeeToView.setIsActivated(false);

            employeeSessionBean.resetEmployeePasswordByAdmin(employeeToView);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully reset employee's password! Please inform employee that OTP has been sent to their email.", null));
        } catch (ResetEmployeePasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doDelete() {
        if (this.currentEmployee.getEmployeeId().equals(this.employeeToView.getEmployeeId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You cannot delete your own account!", null));
        } else {
            try {
                employeeSessionBean.deleteEmployee(this.employeeToView.getEmployeeId());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully deleted employee!", null));
                this.isDeleted = true;
                this.isEditMode = false;
            } catch (DeleteEmployeeException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            }
        }
    }

    public void doCancel() {
        this.isEditMode = false;
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        this.employeeToView = employeeToView;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
