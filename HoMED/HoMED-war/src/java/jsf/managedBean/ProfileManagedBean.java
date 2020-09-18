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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import util.enumeration.GenderEnum;
import util.exceptions.EmployeeInvalidPasswordException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.PasswordsDoNotMatchException;
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
    private GenderEnum male;
    private GenderEnum female;

    /**
     * Creates a new instance of profileManagedBean
     */
    public ProfileManagedBean() {
        employee = new Employee();
        this.male = GenderEnum.MALE;
        this.female = GenderEnum.FEMALE;
    }

    @PostConstruct
    public void PostConstruct() {
        this.employee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        System.out.println("******************NRIC is: " + this.employee.getNric());
    }

    public void updateProfile(ActionEvent actionEvent) {
        System.out.println("Update profile called");
        try {
            employeeSessionBean.updateEmployee(employee);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile updated successfully", null));
        } catch (EmployeeNotFoundException ex) {
            Logger.getLogger(ProfileManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UpdateEmployeeException ex) {
            Logger.getLogger(ProfileManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InputDataValidationException ex) {
            Logger.getLogger(ProfileManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updatePassword(ActionEvent event) {
        try {
            System.out.println("********Update Password called");
            if (!newPassword.equals(confirmNewPassword)) {
                throw new PasswordsDoNotMatchException();
            } else {
                employeeSessionBean.changePassword(employee.getNric(), oldPassword, newPassword);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password updated successfully", null));
            System.out.println("********password changed successfully");
        } catch (PasswordsDoNotMatchException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "New passwords do not match.", null));
        } catch (EmployeeInvalidPasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old password is incorrect.", null));
        } catch (EmployeeNotFoundException ex) {
            Logger.getLogger(ProfileManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public GenderEnum getMale() {
        return male;
    }

    public void setMale(GenderEnum male) {
        this.male = male;
    }

    public GenderEnum getFemale() {
        return female;
    }

    public void setFemale(GenderEnum female) {
        this.female = female;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

}
