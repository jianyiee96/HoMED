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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
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
    public void sendPushNotification(String title, String body, String svcmanFCMToken) {
        try {
            HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", "key=AAAAy-mg1R8:APA91bHBZCoOGcuUDB4CWpCgusKaWuDE4QcR6URnrK6i6WtuCP1xDe7cX9Byv55CXsUdsfSJvf9sT6IOK9EqehNSBySxEoePmZc6W3FOndb08zcFF9_G98JnH4YMNuSLaYvRe3h6QFqx");

            JSONObject message = new JSONObject();
            message.put("to", svcmanFCMToken);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            message.put("notification", notification);
            post.setEntity(new StringEntity(message.toString(), "UTF-8"));

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(post);
            System.out.println(response);
        } catch (Exception ex) {
            System.out.println(ex);
        }
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

        List<Notification> notifications = query.getResultList();

        if (markAsFetched) {
            notifications.forEach(n -> n.setIsFetched(true));
        }

        return notifications;
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
