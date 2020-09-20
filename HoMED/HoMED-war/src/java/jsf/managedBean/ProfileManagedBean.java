/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.enumeration.GenderEnum;
import util.exceptions.DuplicateEntryExistsException;
import util.exceptions.EmployeeInvalidPasswordException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.PasswordsDoNotMatchException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateEmployeeException;

/**
 *
 * @author sunag
 */
@Named(value = "profileManagedBean")
@ViewScoped
public class ProfileManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    private Employee employee;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
    private boolean fieldsDisabled;

    /**
     * Creates a new instance of profileManagedBean
     */
    public ProfileManagedBean() {
        employee = new Employee();
    }

    @PostConstruct
    public void PostConstruct() {
        Employee e = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        employee = employeeSessionBean.retrieveEmployeeById(e.getEmployeeId());
        this.fieldsDisabled = true;
    }

    public void updateProfile(ActionEvent actionEvent) {
        try {
            employeeSessionBean.updateEmployee(employee);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.employee);
            cancelEdit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile updated successfully", null));
        } catch (EmployeeNotFoundException | UpdateEmployeeException | InputDataValidationException | DuplicateEntryExistsException ex) {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating your profile: " + ex.getMessage(), null));             
        } catch (UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }

    }

    public void editMode() {
        this.fieldsDisabled = false;
    }

    public void cancelEdit() {
        this.employee = employeeSessionBean.retrieveEmployeeById(employee.getEmployeeId());
        this.fieldsDisabled = true;
    }

    public void updatePassword(ActionEvent event) {
        try {
            if (!newPassword.equals(confirmNewPassword)) {
                throw new PasswordsDoNotMatchException();
            } else if (oldPassword.equals(newPassword)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please input a different new password", null));
            } else {
                employeeSessionBean.changePassword(employee.getNric(), oldPassword, newPassword);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password updated successfully", null));
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.employee);
                oldPassword = "";
                newPassword = "";
                confirmNewPassword = "";
            }
        } catch (PasswordsDoNotMatchException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "New passwords do not match.", null));
        } catch (EmployeeInvalidPasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old password is incorrect", null));
        } catch (EmployeeNotFoundException ex) {
            Logger.getLogger(ProfileManagedBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public boolean isFieldsDisabled() {
        return fieldsDisabled;
    }

    public void setFieldsDisabled(boolean fieldsDisabled) {
        this.fieldsDisabled = fieldsDisabled;
    }

}
