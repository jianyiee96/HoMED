/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Consultation;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import entity.Serviceman;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "servicemanConsultationRecordsManagedBean")
@ViewScoped
public class ServicemanConsultationRecordsManagedBean implements Serializable {

    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    private MedicalBoardSlot selectedMedicalBoardSlot;

    private MedicalBoardCase selectedMedicalBoardCase;
    private Serviceman selectedServiceman;

    private List<Consultation> pastConsultationsForSelectedServiceman;
    private Consultation selectedPastConsultation;

    public ServicemanConsultationRecordsManagedBean() {
        this.pastConsultationsForSelectedServiceman = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        try {
            Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            this.selectedMedicalBoardSlot = (MedicalBoardSlot) flash.get("medicalBoardSlot");

            this.selectedMedicalBoardCase = (MedicalBoardCase) flash.get("selectedMedicalBoardCase");
            this.selectedServiceman = selectedMedicalBoardCase.getConsultation().getBooking().getServiceman();

            this.pastConsultationsForSelectedServiceman = consultationSessionBeanLocal.retrieveCompletedConsultationsByServicemanId(this.selectedServiceman.getServicemanId());
            
            if (this.pastConsultationsForSelectedServiceman.size() != 0) {
                this.selectedPastConsultation = this.pastConsultationsForSelectedServiceman.get(this.pastConsultationsForSelectedServiceman.size() - 1);
            }
            
        } catch (NullPointerException nullPointerException) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
            } catch (IOException iOException) {
                System.out.println(iOException);
            }
        }
    }

    public void back() {
        if (this.selectedMedicalBoardSlot != null) {
            Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            flash.put("medicalBoardSlot", this.selectedMedicalBoardSlot);
            flash.put("selectedMedicalBoardCase", this.selectedMedicalBoardCase);

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("medical-board-session.xhtml");
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", "No medical board slot is provided!"));
        }
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
