/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
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
import util.exceptions.ChangeEmployeePasswordException;
import util.exceptions.EmployeeNotFoundException;
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
        try {
            this.employee = employeeSessionBean.retrieveEmployeeById(e.getEmployeeId());
            this.fieldsDisabled = true;
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage() + " Please reload the page or contact system admin.", null));
        }
    }

    public void updateProfile(ActionEvent actionEvent) {
        try {
            employeeSessionBean.updateEmployee(employee);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.employee);
            cancelEdit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile updated successfully", null));
        } catch (UpdateEmployeeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }

    }

    public void editMode() {
        this.fieldsDisabled = false;
    }

    public void cancelEdit() {
        try {
            this.employee = employeeSessionBean.retrieveEmployeeById(employee.getEmployeeId());
            this.fieldsDisabled = true;
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage() + " Please reload the page or contact system admin.", null));
        }
    }

    public void updatePassword(ActionEvent event) {
        try {
            employeeSessionBean.changeEmployeePassword(employee.getNric(), oldPassword, newPassword, confirmNewPassword);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password updated successfully", null));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", this.employee);
            oldPassword = "";
            newPassword = "";
            confirmNewPassword = "";
        } catch (ChangeEmployeePasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage()    , null));
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
