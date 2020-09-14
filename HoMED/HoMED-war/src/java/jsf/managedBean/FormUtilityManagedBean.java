/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.FormTemplateSessionBeanLocal;
import entity.FormField;
import entity.FormTemplate;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;


@Named(value = "formUtilityManagedBean")
@ViewScoped
public class FormUtilityManagedBean implements Serializable {

    @EJB
    private FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;

    private List<FormTemplate> formTemplates;
    
    private FormTemplate selectedForm;
    
    
    public FormUtilityManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {

        formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();

    }

    public List<FormTemplate> getFormTemplates() {
        return formTemplates;
    }

    public void setFormTemplates(List<FormTemplate> formTemplates) {
        this.formTemplates = formTemplates;
    }

    public FormTemplate getSelectedForm() {
        return selectedForm;
    }

    // Invoked then selected radio button
    public void setSelectedForm(FormTemplate selectedForm) {
        this.selectedForm = selectedForm;
    }
    
    
    
    
}
