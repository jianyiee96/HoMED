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
import org.primefaces.PrimeFaces;

@Named(value = "servicemanConsultationRecordsManagedBean")
@ViewScoped
public class ServicemanConsultationRecordsManagedBean implements Serializable {

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;
    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private List<Serviceman> servicemen;
    private Serviceman selectedServiceman;

    private List<Consultation> pastConsultationsForSelectedServiceman;
    private Consultation selectedPastConsultation;

    public ServicemanConsultationRecordsManagedBean() {
        this.servicemen = new ArrayList<>();
        this.pastConsultationsForSelectedServiceman = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
    }

    public void onSelectServiceman() {
        this.pastConsultationsForSelectedServiceman = consultationSessionBeanLocal.retrieveCompletedConsultationsByServicemanId(this.selectedServiceman.getServicemanId());
        
        if (this.pastConsultationsForSelectedServiceman.isEmpty()) {
            this.selectedServiceman = null;
            PrimeFaces.current().executeScript("PF('dlgNoConsultationRecords').show();");
        }
    }

    public List<Serviceman> getServicemen() {
        return servicemen;
    }

    public void setServicemen(List<Serviceman> servicemen) {
        this.servicemen = servicemen;
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
