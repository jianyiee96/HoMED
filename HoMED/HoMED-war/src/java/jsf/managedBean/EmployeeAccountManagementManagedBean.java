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
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;

@Named(value = "employeeAccountManagementManagedBean")
@ViewScoped
public class EmployeeAccountManagementManagedBean implements Serializable {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @Inject
    private ViewEmployeeManagedBean viewEmployeeManagedBean;

    private List<Employee> employees;

    public EmployeeAccountManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

        employees = employeeSessionBeanLocal.retrieveAllEmployees();

    }

    public void dialogActionListener() {
        
        employees = employeeSessionBeanLocal.retrieveAllEmployees();
        PrimeFaces.current().ajax().update("formAllEmployees:dataTableEmployees");
    }

    public void doViewEmployee(Employee employee) {
        this.viewEmployeeManagedBean.setEmployeeToView(employee);
        this.viewEmployeeManagedBean.init();
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
}
