/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.NotificationSessionBeanLocal;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author User
 */
@Named(value = "notificationBlastManagedBean")
@ViewScoped
public class NotificationBlastManagedBean implements Serializable {

    @EJB
    NotificationSessionBeanLocal notificationSessionBeanLocal;

    String title;
    String message;

    public NotificationBlastManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {

        title = "";
        message = "";

    }

    public void send() {
        System.out.println("send");
        if (title != null && !title.equals("") && message != null && !message.equals("")) {
            System.out.println("send in");
            notificationSessionBeanLocal.broadcastMessage(title, message);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully sent notification", "This notification has been send to all serviceman."));

            title = "";
            message = "";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to sent notification", "Ensure title and message is not empty"));

        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
