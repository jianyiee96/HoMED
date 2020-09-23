package jsf.managedBean;

import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Employee;
import entity.Serviceman;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.DeleteServicemanException;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.UpdateServicemanException;

@Named(value = "manageServicemanManagedBean")
@ViewScoped
public class ManageServicemanManagedBean implements Serializable {

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private Serviceman servicemanToView;

    private Boolean isAdminView;
    private Boolean isEditMode;
    private Boolean isDeleted;

    public ManageServicemanManagedBean() {
        this.servicemanToView = new Serviceman();
    }

    @PostConstruct
    public void postConstruct() {
        init();
        this.isAdminView = false;
        Object objCurrentEmployee = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (objCurrentEmployee != null) {
            Employee currentEmployee = (Employee) objCurrentEmployee;
            if (currentEmployee.getRole() == EmployeeRoleEnum.ADMIN.ADMIN) {
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
            servicemanSessionBeanLocal.updateServiceman(servicemanToView);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated serviceman!", null));
            this.isEditMode = false;
        } catch (UpdateServicemanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doResetPassword() {
        try {
            // isActivated is changed
            this.servicemanToView.setIsActivated(false);

            servicemanSessionBeanLocal.resetServicemanPasswordByAdmin(servicemanToView);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully reset serviceman's password! Please inform serviceman that OTP has been sent to their email.", null));
        } catch (ResetServicemanPasswordException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doDelete() {
        try {
            servicemanSessionBeanLocal.deleteServiceman(this.servicemanToView.getServicemanId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully deleted serviceman!", null));

            this.isDeleted = true;
            this.isEditMode = false;
        } catch (DeleteServicemanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void doCancel() {
        this.isEditMode = false;
    }

    public Serviceman getServicemanToView() {
        return servicemanToView;
    }

    public void setServicemanToView(Serviceman servicemanToView) {
        this.servicemanToView = servicemanToView;
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
