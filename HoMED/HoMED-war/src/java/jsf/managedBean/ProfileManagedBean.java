package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.exceptions.ChangeEmployeePasswordException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.UpdateEmployeeException;

@Named(value = "profileManagedBean")
@ViewScoped
public class ProfileManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    private Employee employee;

    @NotNull(message = "Old Password is required")
    @Size(min = 8, max = 64, message = "Password must be between length 8 to 64")
    private String oldPassword;
    @NotNull(message = "New Password is required")
    @Size(min = 8, max = 64, message = "New Password must be between length 8 to 64")
    private String newPassword;
    @NotNull(message = "Confirm New Password is required")
    @Size(min = 8, max = 64, message = "Confirm Password must be between length 8 to 64")
    private String confirmNewPassword;

    private Boolean isEditContact;
    private Boolean isEditAddress;

    public ProfileManagedBean() {
        this.employee = new Employee();
    }

    @PostConstruct
    public void PostConstruct() {
        this.isEditContact = false;
        this.isEditAddress = false;

        Employee e = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        try {
            this.employee = employeeSessionBean.retrieveEmployeeById(e.getEmployeeId());
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage() + " Please reload the page or contact system admin.", null));
        }
    }

    public void updateProfile() {
        try {
            employeeSessionBean.updateEmployee(employee);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.employee);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile updated successfully", null));
        } catch (UpdateEmployeeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void updatePassword() {
        try {
            employeeSessionBean.changeEmployeePassword(employee.getEmail(), oldPassword, newPassword, confirmNewPassword);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password updated successfully", null));
            this.employee = employeeSessionBean.retrieveEmployeeById(this.employee.getEmployeeId());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.employee);
        } catch (ChangeEmployeePasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage() + " Please reload the page or contact system admin.", null));
        }
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public Boolean getIsEditContact() {
        return isEditContact;
    }

    public void setIsEditContact(Boolean isEditContact) {
        this.isEditContact = isEditContact;
    }

    public Boolean getIsEditAddress() {
        return isEditAddress;
    }

    public void setIsEditAddress(Boolean isEditAddress) {
        this.isEditAddress = isEditAddress;
    }

}
