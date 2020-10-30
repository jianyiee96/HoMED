package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import entity.Consultation;
import entity.Employee;
import entity.MedicalOfficer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import util.enumeration.MedicalBoardTypeEnum;
import util.exceptions.ConvertBookingException;
import util.exceptions.CreateMedicalBoardCaseException;
import util.exceptions.DeferConsultationException;
import util.exceptions.EndConsultationException;
import util.exceptions.InvalidateConsultationException;

@Named(value = "currentConsultationManagedBean")
@ViewScoped
public class CurrentConsultationManagedBean implements Serializable {

    @Inject
    private ManageFormInstanceManagedBean manageFormInstanceManagedBean;

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private MedicalBoardCaseSessionBeanLocal medicalBoardCaseSessionBeanlocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Consultation> servicemanConsultations;

    private MedicalOfficer currentMedicalOfficer;

    private Consultation selectedConsultation;

    private String cancelBookingComments;

    private Boolean currentConsultationTypeConvertedToReview;

    private String currentConsultationReviewAction;

    private String currentConsultationStatement;

    public CurrentConsultationManagedBean() {
        this.servicemanConsultations = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {

        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalOfficer) {
            currentMedicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(currentEmployee.getEmployeeId());
            selectedConsultation = currentMedicalOfficer.getCurrentConsultation();
            currentConsultationReviewAction = "NIL";

            if (selectedConsultation != null && selectedConsultation.getBooking().getIsForReview()) {
                currentConsultationTypeConvertedToReview = null;
            } else {
                currentConsultationTypeConvertedToReview = Boolean.FALSE;
            }
        }

        refreshServicemanConsultations();

    }

    private void refreshServicemanConsultations() {

        if (currentMedicalOfficer != null && currentMedicalOfficer.getCurrentConsultation() != null) {

            this.servicemanConsultations = consultationSessionBeanLocal.retrieveServicemanNonWaitingConsultation(currentMedicalOfficer.getCurrentConsultation().getBooking().getServiceman().getServicemanId());

        }

    }

    public void onRowSelect(SelectEvent<Consultation> event) {
        selectedConsultation = (Consultation) event.getObject();
    }

    public void dialogActionListener() {
        this.selectedConsultation = consultationSessionBeanLocal.retrieveConsultationById(selectedConsultation.getConsultationId());
        refreshServicemanConsultations();
        if (manageFormInstanceManagedBean.getIsReloadMainPage()) {
            PrimeFaces.current().ajax().update("consultationForm:panelGroupForms");
        }
        if (manageFormInstanceManagedBean.getIsSuccessfulSubmit()) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Form Instance Submission", "Successfully submitted " + manageFormInstanceManagedBean.getFormInstanceToView()));
        }
        if (manageFormInstanceManagedBean.getIsSuccessfulSave()) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Form Instance Saved", "Successfully saved form instance" + manageFormInstanceManagedBean.getFormInstanceToView()));
        }
    }

    public void endCurrentConsultation() {

        try {
            Boolean unsignedForms = selectedConsultation.getBooking().getFormInstances().stream().anyMatch(fi -> fi.getSignedBy() == null);
            if (unsignedForms) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Form Instance Unsigned", "Please make sure all forms have been signed"));
            } else {

                // Case 1: Started as general consultation, ended as general consultation
                if (!selectedConsultation.getBooking().getIsForReview() && !this.currentConsultationTypeConvertedToReview) {

                    consultationSessionBeanLocal.endConsultation(selectedConsultation.getConsultationId(), currentMedicalOfficer.getCurrentConsultation().getRemarks(), currentMedicalOfficer.getCurrentConsultation().getRemarksForServiceman());

                } // Case 2: Started as general consultation, converted to review and ended as premedical board review
                else if (!selectedConsultation.getBooking().getIsForReview() && this.currentConsultationTypeConvertedToReview) {

                    bookingSessionBeanLocal.convertBookingToReview(selectedConsultation.getBooking().getBookingId());

                    if (this.currentConsultationReviewAction.equals("MBIP")) {
                        medicalBoardCaseSessionBeanlocal.createMedicalBoardCaseByReview(selectedConsultation.getConsultationId(), MedicalBoardTypeEnum.PRESENCE, currentConsultationStatement);
                    } else if (this.currentConsultationReviewAction.equals("MBIA")) {
                        medicalBoardCaseSessionBeanlocal.createMedicalBoardCaseByReview(selectedConsultation.getConsultationId(), MedicalBoardTypeEnum.ABSENCE, currentConsultationStatement);
                    }
                    
                    consultationSessionBeanLocal.endConsultation(selectedConsultation.getConsultationId(), currentMedicalOfficer.getCurrentConsultation().getRemarks(), currentMedicalOfficer.getCurrentConsultation().getRemarksForServiceman());


                } // Case 3: Started as premedical board review, ended as premedical board review.
                else if (selectedConsultation.getBooking().getIsForReview()) {

                    if (this.currentConsultationReviewAction.equals("MBIP")) {
                        medicalBoardCaseSessionBeanlocal.createMedicalBoardCaseByReview(selectedConsultation.getConsultationId(), MedicalBoardTypeEnum.PRESENCE, currentConsultationStatement);
                    } else if (this.currentConsultationReviewAction.equals("MBIA")) {
                        medicalBoardCaseSessionBeanlocal.createMedicalBoardCaseByReview(selectedConsultation.getConsultationId(), MedicalBoardTypeEnum.ABSENCE, currentConsultationStatement);
                    }

                    consultationSessionBeanLocal.endConsultation(selectedConsultation.getConsultationId(), currentMedicalOfficer.getCurrentConsultation().getRemarks(), currentMedicalOfficer.getCurrentConsultation().getRemarksForServiceman());


                }

                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("queue-management.xhtml");
                } catch (IOException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
                }
            }
        } catch (EndConsultationException | ConvertBookingException | CreateMedicalBoardCaseException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to end consultation!", ex.getMessage()));
        }
    }

    public void deferCurrentConsultation() {

        try {
            consultationSessionBeanLocal.deferConsultation(selectedConsultation.getConsultationId(), currentMedicalOfficer.getCurrentConsultation().getRemarks(), currentMedicalOfficer.getCurrentConsultation().getRemarksForServiceman());
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("queue-management.xhtml");
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
            }

        } catch (DeferConsultationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to end consultation!", ex.getMessage()));
        }
    }

    public void marknoShowCurrentConsultation() {
        try {
            consultationSessionBeanLocal.invalidateConsultation(selectedConsultation.getConsultationId(), cancelBookingComments);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("queue-management.xhtml");
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
            }
        } catch (InvalidateConsultationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mark Absent Error: " + ex.getMessage(), null));
        }
    }

    public void toggleCurrentConsultationType() {
        this.currentConsultationTypeConvertedToReview = !this.currentConsultationTypeConvertedToReview;
    }

    public Boolean getIsCurrentConsultation() {
        return selectedConsultation.equals(currentMedicalOfficer.getCurrentConsultation());
    }

    public List<Consultation> getServicemanConsultations() {
        return servicemanConsultations;
    }

    public void setServicemanConsultations(List<Consultation> servicemanConsultations) {
        this.servicemanConsultations = servicemanConsultations;
    }

    public MedicalOfficer getCurrentMedicalOfficer() {
        return currentMedicalOfficer;
    }

    public void setCurrentMedicalOfficer(MedicalOfficer currentMedicalOfficer) {
        this.currentMedicalOfficer = currentMedicalOfficer;
    }

    public Consultation getSelectedConsultation() {
        return selectedConsultation;
    }

    public void setSelectedConsultation(Consultation selectedConsultation) {
        this.selectedConsultation = selectedConsultation;
    }

    public ManageFormInstanceManagedBean getManageFormInstanceManagedBean() {
        return manageFormInstanceManagedBean;
    }

    public String getCancelBookingComments() {
        return cancelBookingComments;
    }

    public void setCancelBookingComments(String cancelBookingComments) {
        this.cancelBookingComments = cancelBookingComments;
    }

    public Boolean getCurrentConsultationTypeConvertedToReview() {
        return currentConsultationTypeConvertedToReview;
    }

    public void setCurrentConsultationTypeConvertedToReview(Boolean currentConsultationTypeConvertedToReview) {
        this.currentConsultationTypeConvertedToReview = currentConsultationTypeConvertedToReview;
    }

    public String getCurrentConsultationReviewAction() {
        return currentConsultationReviewAction;
    }

    public void setCurrentConsultationReviewAction(String currentConsultationReviewAction) {
        this.currentConsultationReviewAction = currentConsultationReviewAction;
    }

    public String getCurrentConsultationStatement() {
        return currentConsultationStatement;
    }

    public void setCurrentConsultationStatement(String currentConsultationStatement) {
        this.currentConsultationStatement = currentConsultationStatement;
    }

}
