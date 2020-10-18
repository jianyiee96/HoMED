/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Serviceman;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "pastConsultationsManagedBean")
@ViewScoped
public class PastConsultationsManagedBean implements Serializable {

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private List<Serviceman> servicemenWithPastConsultations;

    public PastConsultationsManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        this.servicemenWithPastConsultations = servicemanSessionBeanLocal.retrieveAllServicemenWithPastConsultations();
    }

    public List<Serviceman> getServicemenWithPastConsultations() {
        return servicemenWithPastConsultations;
    }

    public void setServicemenWithPastConsultations(List<Serviceman> servicemenWithPastConsultations) {
        this.servicemenWithPastConsultations = servicemenWithPastConsultations;
    }

}
