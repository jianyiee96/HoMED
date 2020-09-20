/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;
import util.exceptions.DuplicateEntryExistsException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "employeeAccountManagementManagedBean")
@ViewScoped
public class EmployeeAccountManagementManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @Inject
    private ViewEmployeeManagedBean viewEmployeeManagedBean;

    private List<Employee> employees;

    private Employee employeeToCreate;
    private Boolean isEditableCreateEmployee;

    public EmployeeAccountManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

        employees = employeeSessionBeanLocal.retrieveAllEmployees();

    }

    public void dialogActionListener() {

        this.employees = employeeSessionBeanLocal.retrieveAllEmployees();
        PrimeFaces.current().ajax().update("formAllEmployees:dataTableEmployees");
    }

    public void doViewEmployee(Employee employee) {
        this.viewEmployeeManagedBean.setEmployeeToView(employee);
        this.viewEmployeeManagedBean.init();
    }

    public void doCreateEmployee() {
        this.employeeToCreate = new Employee();
        this.isEditableCreateEmployee = true;
    }

    public void createEmployee() {
        try {
            employeeSessionBeanLocal.createEmployee(employeeToCreate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created Employee! Please inform employee that OTP has been sent to their email.", null));
            this.isEditableCreateEmployee = false;
            this.employees = employeeSessionBeanLocal.retrieveAllEmployees();
        } catch (DuplicateEntryExistsException | InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public ViewEmployeeManagedBean getViewEmployeeManagedBean() {
        return viewEmployeeManagedBean;
    }

    public void setViewEmployeeManagedBean(ViewEmployeeManagedBean viewEmployeeManagedBean) {
        this.viewEmployeeManagedBean = viewEmployeeManagedBean;
    }

    public Employee getEmployeeToCreate() {
        return employeeToCreate;
    }

    public void setEmployeeToCreate(Employee employeeToCreate) {
        this.employeeToCreate = employeeToCreate;
    }

    public GenderEnum[] getGenders() {
        return GenderEnum.values();
    }

    public EmployeeRoleEnum[] getEmployeeRoles() {
        return EmployeeRoleEnum.values();
    }

    public Boolean getIsEditableCreateEmployee() {
        return isEditableCreateEmployee;
    }

    public void setIsEditableCreateEmployee(Boolean isEditableCreateEmployee) {
        this.isEditableCreateEmployee = isEditableCreateEmployee;
    }

}
