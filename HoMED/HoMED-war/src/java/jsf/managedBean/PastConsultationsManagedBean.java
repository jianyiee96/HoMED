/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Consultation;
import entity.Serviceman;
import java.io.Serializable;
import java.util.ArrayList;
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
    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private List<Serviceman> servicemenWithPastConsultations;
    private Serviceman selectedServiceman;

    private List<Consultation> pastConsultationsForSelectedServiceman;
    private Consultation selectedPastConsultation;

    public PastConsultationsManagedBean() {
        this.servicemenWithPastConsultations = new ArrayList<>();
        this.pastConsultationsForSelectedServiceman = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.servicemenWithPastConsultations = servicemanSessionBeanLocal.retrieveAllServicemenWithPastConsultations();
    }

    public void onSelectServiceman() {
        this.pastConsultationsForSelectedServiceman = consultationSessionBeanLocal.retrieveCompletedConsultationsByServicemanId(this.selectedServiceman.getServicemanId());
    }

    public List<Serviceman> getServicemenWithPastConsultations() {
        return servicemenWithPastConsultations;
    }

    public void setServicemenWithPastConsultations(List<Serviceman> servicemenWithPastConsultations) {
        this.servicemenWithPastConsultations = servicemenWithPastConsultations;
    }

    public Serviceman getSelectedServiceman() {
        return selectedServiceman;
    }

    public void setSelectedServiceman(Serviceman selectedServiceman) {
        this.selectedServiceman = selectedServiceman;
    }

    public List<Consultation> getPastConsultationsForSelectedServiceman() {
        return pastConsultationsForSelectedServiceman;
    }

    public void setPastConsultationsForSelectedServiceman(List<Consultation> pastConsultationsForSelectedServiceman) {
        this.pastConsultationsForSelectedServiceman = pastConsultationsForSelectedServiceman;
    }

    public Consultation getSelectedPastConsultation() {
        return selectedPastConsultation;
    }

    public void setSelectedPastConsultation(Consultation selectedPastConsultation) {
        this.selectedPastConsultation = selectedPastConsultation;
    }

}
