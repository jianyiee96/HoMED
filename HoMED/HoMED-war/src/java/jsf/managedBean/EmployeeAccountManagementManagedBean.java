package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;

@Named(value = "employeeAccountManagementManagedBean")
@ViewScoped
public class EmployeeAccountManagementManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @Inject
    private ManageEmployeeManagedBean manageEmployeeManagedBean;

    private List<Employee> employees;

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

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public ManageEmployeeManagedBean getManageEmployeeManagedBean() {
        return manageEmployeeManagedBean;
    }

    public void setManageEmployeeManagedBean(ManageEmployeeManagedBean manageEmployeeManagedBean) {
        this.manageEmployeeManagedBean = manageEmployeeManagedBean;
    }

    public GenderEnum[] getGenders() {
        return GenderEnum.values();
    }

    public EmployeeRoleEnum[] getEmployeeRoles() {
        return EmployeeRoleEnum.values();
    }

}
