package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalOfficer;
import entity.MedicalStaff;
import entity.Serviceman;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.AssignMedicalStaffToMedicalCentreException;
import util.exceptions.CreateEmployeeException;
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.ResetEmployeePasswordException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateEmployeeException;
import util.exceptions.UpdateMedicalOfficerChairmanStatusException;

@Named(value = "manageEmployeeManagedBean")
@ViewScoped
public class ManageEmployeeManagedBean implements Serializable {

    @EJB
    private MedicalCentreSessionBeanLocal medicalCentreSessionBean;

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    private Employee currentEmployee;

    private Employee employeeToView;

    private Boolean isCreateState;

    private Serviceman servicemanToCopy;

    private List<MedicalCentre> medicalCentres;
    private HashMap<Long, MedicalCentre> medicalCentresHm;

    private Long medicalCentreId;
    private Boolean isMedicalOfficerChairman;

    private Boolean isAdminView;
    private Boolean isEditMode;
    private Boolean isHideAdminPanel;

    public ManageEmployeeManagedBean() {
        this.employeeToView = new Employee();
        this.medicalCentres = new ArrayList<>();
        this.medicalCentresHm = new HashMap<>();
    }

    @PostConstruct
    public void postConstruct() {
        init();
        this.isCreateState = false;
        this.isAdminView = false;
        this.servicemanToCopy = null;
        Object objCurrentEmployee = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        this.medicalCentres = medicalCentreSessionBean.retrieveAllMedicalCentres();
        for (MedicalCentre mc : medicalCentres) {
            this.medicalCentresHm.put(mc.getMedicalCentreId(), mc);
        }

        if (objCurrentEmployee != null) {
            this.currentEmployee = (Employee) objCurrentEmployee;
            if (this.currentEmployee.getRole() == EmployeeRoleEnum.SUPER_USER) {
                this.isAdminView = true;
            }
        }
    }

    private void init() {
        // Wee Keat to Bryan: Added this to ensure the ID is refreshed whenever a new employee is selected.
        this.medicalCentreId = null;
        this.isMedicalOfficerChairman = null;
        this.isHideAdminPanel = false;
        this.servicemanToCopy = null;
    }

    public void initCreate() {
        init();
        this.employeeToView = new Employee();
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
            employeeSessionBean.createEmployee(employeeToView);

            // Added this to retrieve the newly created Employee
            employeeToView = employeeSessionBean.retrieveEmployeeByEmail(employeeToView.getEmail());

            if (employeeToView instanceof MedicalStaff) {
                employeeSessionBean.assignMedicalStaffToMedicalCentre(employeeToView.getEmployeeId(), medicalCentreId);
            }
            if (employeeToView instanceof MedicalOfficer) {
                employeeSessionBean.updateMedicalOfficerChairmanStatus(employeeToView.getEmployeeId(), isMedicalOfficerChairman);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created Employee! Please inform employee that OTP has been sent to their email.", null));
            this.isHideAdminPanel = true;
            this.isEditMode = false;
        } catch (CreateEmployeeException | AssignMedicalStaffToMedicalCentreException | UpdateMedicalOfficerChairmanStatusException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unexpected errors occurred. Please contact System Admin!", null));
        }
    }

    public void doSave() {
        try {
            employeeSessionBean.updateEmployee(employeeToView);
            if (employeeToView instanceof MedicalStaff) {
                employeeSessionBean.assignMedicalStaffToMedicalCentre(employeeToView.getEmployeeId(), medicalCentreId);
            }
            if (employeeToView instanceof MedicalOfficer) {
                employeeSessionBean.updateMedicalOfficerChairmanStatus(employeeToView.getEmployeeId(), isMedicalOfficerChairman);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated employee!", null));
            this.isEditMode = false;
        } catch (UpdateEmployeeException | AssignMedicalStaffToMedicalCentreException | UpdateMedicalOfficerChairmanStatusException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doResetPassword() {
        try {
            this.employeeToView.setIsActivated(false);

            employeeSessionBean.resetEmployeePasswordBySuperUser(employeeToView);

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
                this.isHideAdminPanel = true;
                this.isEditMode = false;
            } catch (DeleteEmployeeException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            }
        }
    }

    public void doReset() {
        try {
            setEmployeeToView(employeeSessionBean.retrieveEmployeeById(this.employeeToView.getEmployeeId()));
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void emailValidation() {
        try {
            this.servicemanToCopy = servicemanSessionBean.retrieveServicemanByEmail(this.employeeToView.getEmail());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Serviceman with same email detected. All details will also be updated on serviceman account.", null));
        } catch (ServicemanNotFoundException ex) {
            this.servicemanToCopy = null;
        }
    }

    public void doCopyDetails() {
        this.employeeToView.setName(servicemanToCopy.getName());
        this.employeeToView.setGender(servicemanToCopy.getGender());
        this.employeeToView.setPhoneNumber(servicemanToCopy.getPhoneNumber());
        this.employeeToView.setAddress(servicemanToCopy.getAddress());
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        if (employeeToView instanceof MedicalStaff) {
            MedicalCentre mc = ((MedicalStaff) employeeToView).getMedicalCentre();
            if (mc != null) {
                this.medicalCentreId = mc.getMedicalCentreId();
            }
        }
        if (employeeToView instanceof MedicalOfficer) {
            this.isMedicalOfficerChairman = ((MedicalOfficer) employeeToView).getIsChairman();
        }
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

    public Serviceman getServicemanToCopy() {
        return servicemanToCopy;
    }

    public Long getMedicalCentreId() {
        return medicalCentreId;
    }

    public void setMedicalCentreId(Long medicalCentreId) {
        this.medicalCentreId = medicalCentreId;
    }

    public Boolean getIsMedicalOfficerChairman() {
        return isMedicalOfficerChairman;
    }

    public void setIsMedicalOfficerChairman(Boolean isMedicalOfficerChairman) {
        this.isMedicalOfficerChairman = isMedicalOfficerChairman;
    }

    public List<MedicalCentre> getMedicalCentres() {
        return medicalCentres;
    }

    public void setMedicalCentres(List<MedicalCentre> medicalCentres) {
        this.medicalCentres = medicalCentres;
    }

    public HashMap<Long, MedicalCentre> getMedicalCentresHm() {
        return medicalCentresHm;
    }

}
