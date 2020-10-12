/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Slot implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date startDateTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date endDateTime;

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }
    
    public Date getEndDateTime() {
        return endDateTime;
    }

    public int getStartHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDateTime);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    public int getEndHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.endDateTime);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (slotId != null ? slotId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the slotId fields are not set
        if (!(object instanceof Slot)) {
            return false;
        }
        Slot other = (Slot) object;
        if ((this.slotId == null && other.slotId != null) || (this.slotId != null && !this.slotId.equals(other.slotId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Slot[ id=" + slotId + " ]";
    }
    
}
