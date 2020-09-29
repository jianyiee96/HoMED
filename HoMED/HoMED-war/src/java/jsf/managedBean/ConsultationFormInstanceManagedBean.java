package jsf.managedBean;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import entity.FormInstance;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "consultationFormInstanceManagedBean")
@ViewScoped
public class ConsultationFormInstanceManagedBean implements Serializable {

    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBeanLocal;

    private FormInstance formInstanceToView;

    public ConsultationFormInstanceManagedBean() {
        this.formInstanceToView = new FormInstance();
    }

    @PostConstruct
    public void postConstruct() {
        this.formInstanceToView = formInstanceSessionBeanLocal.retrieveFormInstance(1l);
    }

    public void init() {

    }

    public FormInstance getFormInstanceToView() {
        return formInstanceToView;
    }

    public void setFormInstanceToView(FormInstance formInstanceToView) {
        this.formInstanceToView = formInstanceToView;
    }

}
