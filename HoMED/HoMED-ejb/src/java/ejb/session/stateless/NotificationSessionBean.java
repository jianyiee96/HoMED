/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Notification;
import entity.Serviceman;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.CreateNotificationException;
import util.exceptions.ServicemanNotFoundException;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Stateless
public class NotificationSessionBean implements NotificationSessionBeanLocal {

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public NotificationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewNotification(Notification newNotification, Long servicemanId, Boolean sendEmail) throws CreateNotificationException {
        String errorMessage = "Failed to create Notification: ";
        try {

            Set<ConstraintViolation<Notification>> constraintViolations = validator.validate(newNotification);

            if (constraintViolations.isEmpty()) {
                Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(servicemanId);

                newNotification.setServiceman(serviceman);
                serviceman.getNotifications().add(newNotification);
                em.persist(newNotification);
                em.flush();

                return newNotification.getNotificationId();
            } else {
                System.out.println("caught");
                throw new CreateNotificationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (ServicemanNotFoundException ex) {
            throw new CreateNotificationException(errorMessage + "Unable to retrieve serviceman account.");
        }
    }

    @Override
    public List<Notification> retrieveAllNotificationsByServicemanId(Long servicemanId, Boolean markAsFetched) {
        Query query = em.createQuery("SELECT n FROM Notification n WHERE n.serviceman.servicemanId = :inServicemanId");
        query.setParameter("inServicemanId", servicemanId);

        if (markAsFetched) {
            List<Notification> notifications = query.getResultList();

            notifications.forEach(n -> n.setIsFetched(true));
        }

        return query.getResultList();
    }

    @Override
    public List<Notification> retrieveAllUnfetchedNotificationsByServicemanId(Long servicemanId) {
        Query query = em.createQuery("SELECT n FROM Notification n WHERE n.serviceman.servicemanId = :inServicemanId AND n.isFetched = FALSE");
        query.setParameter("inServicemanId", servicemanId);

        return query.getResultList();
    }

    @Override
    public void readNotification(Long notificationId) {
        Notification n = em.find(Notification.class, notificationId);

        if (n != null) {

            if (!n.getIsRead()) {
                n.setIsRead(true);
            }

        }
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification n = em.find(Notification.class, notificationId);

        if (n != null) {
            n.getServiceman().getNotifications().remove(n);
            em.remove(n);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Notification>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

}
