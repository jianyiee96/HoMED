/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class BookingSlot extends Slot implements Serializable {

    @OneToOne
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private MedicalCentre medicalCentre;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public MedicalCentre getMedicalCentre() {
        return medicalCentre;
    }

    public void setMedicalCentre(MedicalCentre medicalCentre) {
        this.medicalCentre = medicalCentre;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookingSlot)) {
            return false;
        }
        BookingSlot other = (BookingSlot) object;
        if ((this.getSlotId() == null && other.getSlotId() != null) || (this.getSlotId() != null && !this.getSlotId().equals(other.getSlotId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BookingSlot[ id=" + this.getSlotId() + " ]";
    }
    
}
