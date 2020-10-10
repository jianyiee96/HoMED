/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import entity.MedicalCentre;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.MedicalCentreNotFoundException;

@Named(value = "medicalCentreStaffManagementManagedBean")
@ViewScoped
public class MedicalCentreStaffManagementManagedBean implements Serializable {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;

    private Long medicalCentreToViewId;
    private MedicalCentre medicalCentreToView;

    public MedicalCentreStaffManagementManagedBean() {
        try {
            this.medicalCentreToViewId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("medicalCentreToViewId"));
        } catch (NumberFormatException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Centre Staff Management", "Invalid Medical Centre ID"));
        }
    }

    @PostConstruct
    public void postConstruct() {
        if (this.medicalCentreToViewId != null) {
            try {
                this.medicalCentreToView = this.medicalCentreSessionBeanLocal.retrieveMedicalCentreById(medicalCentreToViewId);
            } catch (MedicalCentreNotFoundException ex) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Centre Staff Management", ex.getMessage()));
            }
        }
    }

    public Long getMedicalCentreToViewId() {
        return medicalCentreToViewId;
    }

    public void setMedicalCentreToViewId(Long medicalCentreToViewId) {
        this.medicalCentreToViewId = medicalCentreToViewId;
    }

    public MedicalCentre getMedicalCentreToView() {
        return medicalCentreToView;
    }

    public void setMedicalCentreToView(MedicalCentre medicalCentreToView) {
        this.medicalCentreToView = medicalCentreToView;
    }

}
