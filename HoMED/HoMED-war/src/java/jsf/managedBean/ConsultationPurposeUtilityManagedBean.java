/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import entity.ConsultationPurpose;
import entity.FormTemplate;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.CreateConsultationPurposeException;
import util.exceptions.CreateFormTemplateException;

@Named(value = "consultationPurposeUtilityManagedBean")
@ViewScoped
public class ConsultationPurposeUtilityManagedBean implements Serializable {

    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    private List<ConsultationPurpose> consultationPurposes;
    
    private ConsultationPurpose selectedConsultationPurpose;
    
    private String createConsultationPurposeName;
    
    public ConsultationPurposeUtilityManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
    }
    
    public void createConsultationPurpose(){
        try {
            if (this.createConsultationPurposeName != null || !this.createConsultationPurposeName.equals("")) {
                consultationPurposeSessionBeanLocal.createConsultationPurpose(new ConsultationPurpose(this.createConsultationPurposeName));
                this.createConsultationPurposeName = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created new consultation purpose!", "Database has been updated."));
                this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
            }

        } catch (CreateConsultationPurposeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to create consultation purpose!", ex.getMessage()));

        }
        
    }
    

    public List<ConsultationPurpose> getConsultationPurposes() {
        return consultationPurposes;
    }

    public void setConsultationPurposes(List<ConsultationPurpose> consultationPurposes) {
        this.consultationPurposes = consultationPurposes;
    }

    public ConsultationPurpose getSelectedConsultationPurpose() {
        return selectedConsultationPurpose;
    }

    public void setSelectedConsultationPurpose(ConsultationPurpose selectedConsultationPurpose) {
        this.selectedConsultationPurpose = selectedConsultationPurpose;
    }

    public String getCreateConsultationPurposeName() {
        return createConsultationPurposeName;
    }

    public void setCreateConsultationPurposeName(String createConsultationPurposeName) {
        this.createConsultationPurposeName = createConsultationPurposeName;
    }
    

}
