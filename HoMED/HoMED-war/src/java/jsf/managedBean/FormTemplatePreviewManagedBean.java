package jsf.managedBean;

import entity.FormTemplate;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "formTemplatePreviewManagedBean")
@ViewScoped
public class FormTemplatePreviewManagedBean implements Serializable {

    private FormTemplate formTemplateToView;

    public FormTemplatePreviewManagedBean() {
        this.formTemplateToView = new FormTemplate();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public FormTemplate getFormTemplateToView() {
        return formTemplateToView;
    }

    public void setFormTemplateToView(FormTemplate formTemplateToView) {
        this.formTemplateToView = formTemplateToView;
    }

}
