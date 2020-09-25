/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import util.enumeration.DayOfWeekEnum;

@Entity
public class OperatingHours implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operatingHoursId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Day of week must be provided")
    private DayOfWeekEnum dayOfWeek;

    @Column(nullable = false)
    @NotNull(message = "Operating status must be provided")
    private Boolean isClose;

    private LocalTime openingHours;
    private LocalTime closingHours;

    public OperatingHours() {
    }

    public OperatingHours(DayOfWeekEnum dayOfWeek, Boolean isClose, LocalTime openingHours, LocalTime closingHours) {
        this.dayOfWeek = dayOfWeek;
        this.isClose = isClose;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
    }
    
    public Long getOperatingHoursId() {
        return operatingHoursId;
    }

    public void setOperatingHoursId(Long operatingHoursId) {
        this.operatingHoursId = operatingHoursId;
    }

    public DayOfWeekEnum getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeekEnum dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Boolean getIsClose() {
        return isClose;
    }

    public void setIsClose(Boolean isClose) {
        this.isClose = isClose;

        if (isClose) {
            this.openingHours = null;
            this.closingHours = null;
        }
    }

    public LocalTime getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(LocalTime openingHours) {
        this.openingHours = openingHours;
    }
    
    public LocalTime getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(LocalTime closingHours) {
        this.closingHours = closingHours;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (operatingHoursId != null ? operatingHoursId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the operatingHoursId fields are not set
        if (!(object instanceof OperatingHours)) {
            return false;
        }
        OperatingHours other = (OperatingHours) object;
        if ((this.operatingHoursId == null && other.operatingHoursId != null) || (this.operatingHoursId != null && !this.operatingHoursId.equals(other.operatingHoursId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OperatingHours[ id=" + operatingHoursId + " ]";
    }

}
