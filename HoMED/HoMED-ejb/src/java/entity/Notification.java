/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Entity
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date notificationDate;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Notification title must be between length 1 to 128")
    @Size(min = 1, max = 128, message = "Notification title must be between length 1 to 128")
    private String title;

    @Column(nullable = false, length = 8000)
    @NotNull(message = "Notification message must be between length 1 to 8000")
    @Size(min = 1, max = 8000, message = "Notification message must be between length 1 to 8000")
    private String message;

    @Column(nullable = false)
    @NotNull
    private Boolean isRead;

    @Column(nullable = false)
    @NotNull
    private Boolean isFetched;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Serviceman serviceman;

    public Notification() {
        this.notificationDate = new Date();
        this.isRead = false;
        this.isFetched = false;
    }

    public Notification(String title, String message) {
        this();
        this.title = title;
        this.message = message;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
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

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsFetched() {
        return isFetched;
    }

    public void setIsFetched(Boolean isFetched) {
        this.isFetched = isFetched;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationId != null ? notificationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the notificationId fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.notificationId == null && other.notificationId != null) || (this.notificationId != null && !this.notificationId.equals(other.notificationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Notification[ id=" + notificationId + " ]";
    }

}
