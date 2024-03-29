/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConditionStatus;
import entity.Consultation;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import entity.Notification;
import entity.Serviceman;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.MedicalBoardCaseStatusEnum;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.enumeration.NotificationTypeEnum;
import util.enumeration.PesStatusEnum;
import util.exceptions.CreateMedicalBoardCaseException;
import util.exceptions.CreateNotificationException;
import util.exceptions.SignMedicalBoardCaseException;
import util.exceptions.UpdateMedicalBoardSlotException;

@Stateless
public class MedicalBoardCaseSessionBean implements MedicalBoardCaseSessionBeanLocal {

    @EJB(name = "NotificationSessionBeanLocal")
    private NotificationSessionBeanLocal notificationSessionBeanLocal;

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public void createMedicalBoardCaseByReview(Long consultationId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException {

        Consultation consultation = consultationSessionBeanLocal.retrieveConsultationById(consultationId);

        if (consultation == null) {
            throw new CreateMedicalBoardCaseException("Invalid Consultation Id");
        } else if (!consultation.getBooking().getIsForReview()) {
            throw new CreateMedicalBoardCaseException("Invalid Consultation Type: Booking for consultation is not for Pre Medical Board Review");
        } else if (statementOfCase == null || statementOfCase.isEmpty()) {
            throw new CreateMedicalBoardCaseException("Unable to create Medical Board Case: Please supply a statement of case");
        } else if (consultation.getMedicalBoardCase() != null) {
            throw new CreateMedicalBoardCaseException("Unable to create Medical Board Case: Current consultation is already tied to an existing medical board case");
        }

        MedicalBoardCase medicalBoardCase = new MedicalBoardCase(consultation, medicalBoardType, statementOfCase);
        consultation.setMedicalBoardCase(medicalBoardCase);

        em.persist(medicalBoardCase);
        em.flush();

        Notification n = new Notification("New Medical Board Case", "You have been assigned a medical board case. You may view the details now.", NotificationTypeEnum.MEDICAL_BOARD, medicalBoardCase.getMedicalBoardCaseId());
        try {
            notificationSessionBeanLocal.createNewNotification(n, medicalBoardCase.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
            notificationSessionBeanLocal.sendPushNotification("New Medical Board Case", "You have been assigned a medical board case. You may view the details now.", medicalBoardCase.getConsultation().getBooking().getServiceman().getFcmToken());
        } catch (CreateNotificationException ex) {
            System.out.println("Error in creating notification " + ex);
        }
    }

    @Override
    public void createMedicalBoardCaseByBoard(Long predecessorMedicalBoardCaseId, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) throws CreateMedicalBoardCaseException {

        MedicalBoardCase predecessorMedicalBoardCase = this.retrieveMedicalBoardCaseById(predecessorMedicalBoardCaseId);

        if (predecessorMedicalBoardCase == null) {
            throw new CreateMedicalBoardCaseException("Invalid Medical Board Id");
        } else if (statementOfCase == null || statementOfCase.isEmpty()) {
            throw new CreateMedicalBoardCaseException("Unable to create Medical Board Case: Please supply a statement of case");
        } else if (predecessorMedicalBoardCase.getFollowUpMedicalBoardCase() != null) {
            throw new CreateMedicalBoardCaseException("Unable to create Medical Board Case: Current Medical Board is already tied to an existing follow up medical board case");
        }

        MedicalBoardCase medicalBoardCase = new MedicalBoardCase(predecessorMedicalBoardCase, medicalBoardType, statementOfCase);
        predecessorMedicalBoardCase.setFollowUpMedicalBoardCase(medicalBoardCase);

        em.persist(medicalBoardCase);
        em.flush();

        Notification n = new Notification("Follow up Medical Board Case", "A  follow up follow up medical board case has been scheduled for you. You may view the details now.", NotificationTypeEnum.MEDICAL_BOARD, medicalBoardCase.getMedicalBoardCaseId());
        try {
            notificationSessionBeanLocal.createNewNotification(n, medicalBoardCase.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
            notificationSessionBeanLocal.sendPushNotification("Follow up Medical Board Case", "A  follow up follow up medical board case has been scheduled for you. You may view the details now.", medicalBoardCase.getConsultation().getBooking().getServiceman().getFcmToken());
        } catch (CreateNotificationException ex) {
            System.out.println("Error in creating notification " + ex);
        }

    }

    @Override
    public void signMedicalBoardCase(Long medicalBoardCaseId, String boardFindings, PesStatusEnum newPesStatus, List<ConditionStatus> conditionStatuses) throws SignMedicalBoardCaseException {

        MedicalBoardCase medicalBoardCase = retrieveMedicalBoardCaseById(medicalBoardCaseId);

        if (medicalBoardCase == null) {
            throw new SignMedicalBoardCaseException("Invalid Medical Board Case Id");
        } else if (medicalBoardCase.getIsSigned()) {
            throw new SignMedicalBoardCaseException("Unable to sign medical board case: Case is signed already");
        } else if (boardFindings == null || boardFindings.isEmpty()) {
            throw new SignMedicalBoardCaseException("Unable to sign medical board case: Please include board findings.");
        }

        try {
            medicalBoardCase.setIsSigned(Boolean.TRUE);
            medicalBoardCase.setBoardFindings(boardFindings);

            Serviceman serviceman = medicalBoardCase.getConsultation().getBooking().getServiceman();

            if (newPesStatus != null) {
                medicalBoardCase.setFinalPesStatus(newPesStatus);
                serviceman.setPesStatus(newPesStatus);
            } else {
                medicalBoardCase.setFinalPesStatus(serviceman.getPesStatus());
            }

            for (ConditionStatus cs : serviceman.getConditionStatuses()) {
                cs.setIsActive(Boolean.FALSE);
            }

            for (ConditionStatus cs : conditionStatuses) {
                em.persist(cs);
                cs.setServiceman(serviceman);
                serviceman.getConditionStatuses().add(cs);
                cs.setMedicalBoardCase(medicalBoardCase);
                medicalBoardCase.getConditionStatuses().add(cs);
                em.flush();
            }

            emailSessionBeanLocal.emailServicemanBoardResult(serviceman, medicalBoardCase.getFinalPesStatus(), conditionStatuses);

        } catch (Exception ex) {
            throw new SignMedicalBoardCaseException("Unable to sign medical board case: " + ex.getMessage());
        }

    }

    @Override
    public List<MedicalBoardCase> retrieveUnallocatedMedicalBoardCases(MedicalBoardTypeEnum medicalBoardTypeEnum) {
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardSlot IS NULL AND mbc.medicalBoardType = :boardType");
        query.setParameter("boardType", medicalBoardTypeEnum);

        return query.getResultList();
    }

    @Override
    public List<MedicalBoardCase> retrieveMedicalBoardCasesForSelectedMedicalBoardSlot(MedicalBoardTypeEnum medicalBoardTypeEnum, MedicalBoardSlot medicalBoardSlot) {

        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardSlot = :boardSlot AND mbc.medicalBoardType = :boardType");
        query.setParameter("boardSlot", medicalBoardSlot);
        query.setParameter("boardType", medicalBoardTypeEnum);

        return query.getResultList();
    }

    @Override
    public MedicalBoardCase retrieveMedicalBoardCaseById(Long medicalBoardCaseId) {
        MedicalBoardCase medicalBoardCase = em.find(MedicalBoardCase.class, medicalBoardCaseId);
        return medicalBoardCase;
    }

    @Override
    public void allocateMedicalBoardCasesToMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot, List<MedicalBoardCase> medicalBoardCases, List<MedicalBoardCase> addedMedicalBoardCases, List<MedicalBoardCase> removedMedicalBoardCases) throws UpdateMedicalBoardSlotException {
        String errorMessage = "Failed to allocate Medical Board Cases to Medical Board Slot: ";

        try {
            if (medicalBoardSlot != null && medicalBoardSlot.getSlotId() != null) {
                MedicalBoardSlot medicalBoardSlotToUpdate = slotSessionBeanLocal.retrieveMedicalBoardSlotById(medicalBoardSlot.getSlotId());

                medicalBoardSlotToUpdate.setEstimatedTimeForEachBoardInPresenceCase(medicalBoardSlot.getEstimatedTimeForEachBoardInPresenceCase());
                medicalBoardSlotToUpdate.setEstimatedTimeForEachBoardInAbsenceCase(medicalBoardSlot.getEstimatedTimeForEachBoardInAbsenceCase());

                // To reset all the relationship
                for (int i = 0; i < medicalBoardSlotToUpdate.getMedicalBoardCases().size(); i++) {
                    medicalBoardSlotToUpdate.getMedicalBoardCases().get(i).setMedicalBoardSlot(null);
                    medicalBoardSlotToUpdate.getMedicalBoardCases().get(i).setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum.WAITING);
                }
                medicalBoardSlotToUpdate.getMedicalBoardCases().clear();

                medicalBoardCases.forEach(mbc -> {
                    MedicalBoardCase medicalBoardCaseToUpdate = retrieveMedicalBoardCaseById(mbc.getMedicalBoardCaseId());
                    medicalBoardCaseToUpdate.setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum.SCHEDULED);

                    medicalBoardCaseToUpdate.setMedicalBoardSlot(medicalBoardSlotToUpdate);
                    medicalBoardSlotToUpdate.getMedicalBoardCases().add(medicalBoardCaseToUpdate);
                });

                if (medicalBoardSlotToUpdate.getMedicalBoardCases().isEmpty()) {
                    medicalBoardSlotToUpdate.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.ASSIGNED);
                } else {
                    medicalBoardSlotToUpdate.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.ALLOCATED);
                }

                String pattern = "EEEE, d MMM yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                String title = "";
                String body = "";

                for (MedicalBoardCase addedMbc : addedMedicalBoardCases) {
                    MedicalBoardCase mbc = this.retrieveMedicalBoardCaseById(addedMbc.getMedicalBoardCaseId());
                    String dateForBoard = simpleDateFormat.format(mbc.getMedicalBoardSlot().getStartDateTime());

                    if (mbc.getMedicalBoardType() == MedicalBoardTypeEnum.ABSENCE) {
                        title = "Medical Board In-Absence Allocated";
                        body = "Your Medical Board In-Absence Case [ID: " + mbc.getMedicalBoardCaseId() + "] has been scheduled on " + dateForBoard + ".\n\nYou are NOT required to turn up for the medical board.";
                    } else {
                        title = "Medical Board In-Presence Allocated";
                        body = "Your Medical Board In-Presence Case [ID: " + mbc.getMedicalBoardCaseId() + "] has been scheduled on " + dateForBoard + ".\n\nYou are required to turn up for the medical board. Do remember to come early for your medical board.";
                    }

                    Notification n = new Notification(title, body, NotificationTypeEnum.MEDICAL_BOARD, mbc.getMedicalBoardCaseId());
                    notificationSessionBeanLocal.createNewNotification(n, mbc.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.TRUE);
                    notificationSessionBeanLocal.sendPushNotification(title, body, mbc.getConsultation().getBooking().getServiceman().getFcmToken());

                    title = "";
                    body = "";
                }

                for (MedicalBoardCase removedMbc : removedMedicalBoardCases) {
                    String originalDateForBoard = simpleDateFormat.format(removedMbc.getMedicalBoardSlot().getStartDateTime());

                    if (removedMbc.getMedicalBoardType() == MedicalBoardTypeEnum.ABSENCE) {
                        title = "Medical Board In-Absence Updated";
                        body = "Your original Medical Board In-Absence Case [ID: " + removedMbc.getMedicalBoardCaseId() + "] on " + originalDateForBoard + " has been put back into waiting list.\n\nThe medical board admin will schedule another board for you soon.";
                    } else {
                        title = "Medical Board In-Presence Updated";
                        body = "Your original Medical Board In-Presence Case [ID: " + removedMbc.getMedicalBoardCaseId() + "] on " + originalDateForBoard + " has been put back into waiting list.\n\nPlease do NOT turn up for the medical board. The medical board admin will schedule another board for you soon.";
                    }

                    Notification n = new Notification(title, body, NotificationTypeEnum.MEDICAL_BOARD, removedMbc.getMedicalBoardCaseId());
                    notificationSessionBeanLocal.createNewNotification(n, removedMbc.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.TRUE);
                    notificationSessionBeanLocal.sendPushNotification(title, body, removedMbc.getConsultation().getBooking().getServiceman().getFcmToken());

                    title = "";
                    body = "";
                }
            } else {
                throw new UpdateMedicalBoardSlotException(errorMessage + "Medical Board Slot ID not found!");
            }
        } catch (CreateNotificationException ex) {
            System.out.println("[MedicalBoardCaseSessionBean > allocateMedicalBoardCasesToMedicalBoardSlot] Error in creating notification" + ex.getMessage());
        }
    }

    @Override
    public List<MedicalBoardCase> retrieveAllMedicalBoardCases() {
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc");

        return query.getResultList();
    }

    @Override
    public List<MedicalBoardCase> retrieveAllMedicalBoardInPresenceCases() {
        Query query = em.createQuery("SELECT mbc FROM MedicalBoardCase mbc WHERE mbc.medicalBoardType = :type");
        query.setParameter("type", MedicalBoardTypeEnum.PRESENCE);

        return query.getResultList();
    }

    @Override
    public List<MedicalBoardCase> retrieveMedicalBoardCasesByServiceman(Long servicemanId) {

        List<MedicalBoardCase> allMedicalBoardCases = retrieveAllMedicalBoardCases();

        List<MedicalBoardCase> servicemanMbc = new ArrayList<>();

        for (MedicalBoardCase mbc : allMedicalBoardCases) {
            if (mbc.getConsultation().getBooking().getServiceman().getServicemanId().equals(servicemanId)) {
                servicemanMbc.add(mbc);
            }
        }

        return servicemanMbc;

    }
}
