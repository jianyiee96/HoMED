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
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.ResetEmployeePasswordException;
import util.exceptions.UpdateEmployeeException;

@Named(value = "viewEmployeeManagedBean")
@ViewScoped
public class ViewEmployeeManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    private Employee currentEmployee;

    private Employee originalEmployeeToView;
    private Employee employeeToView;

    private Boolean isAdminView;
    private Boolean isEditMode;
    private Boolean isDeleted;

    public ViewEmployeeManagedBean() {
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
            this.originalEmployeeToView = new Employee(employeeToView);
        } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating the employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void doResetPassword() {
        try {
            // isActivated is changed
            this.employeeToView.setIsActivated(false);
            this.originalEmployeeToView = new Employee(this.employeeToView);
            employeeSessionBean.resetEmployeePasswordByAdmin(employeeToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully reset employee's password! Please inform employee that OTP has been sent to their email.", null));
        } catch (ResetEmployeePasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while resetting employee password: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
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
            } catch (DeleteEmployeeException | EmployeeNotFoundException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting the employee: " + ex.getMessage(), null));
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
            }
        }
    }

    public void doCancel() {
        this.isEditMode = false;
        this.employeeToView = new Employee(originalEmployeeToView);
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        this.originalEmployeeToView = new Employee(employeeToView);
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
