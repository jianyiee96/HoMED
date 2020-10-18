/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MedicalBoardSlot extends Slot implements Serializable, Comparable<MedicalBoardSlot> {

    @ManyToOne(optional = true)
    private MedicalBoard medicalBoard;

    public MedicalBoardSlot() {
    }

    public MedicalBoardSlot(Date start, Date end) {
        this();
        super.setStartDateTime(start);
        super.setEndDateTime(end);
    }

    public MedicalBoard getMedicalBoard() {
        return medicalBoard;
    }

    public void setMedicalBoard(MedicalBoard medicalBoard) {
        this.medicalBoard = medicalBoard;
    }

    @Override
    public int compareTo(MedicalBoardSlot another) {

        if (super.getStartDateTime().before(another.getStartDateTime())) {
            return -1;
        } else if (super.getStartDateTime().after(another.getStartDateTime())) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalBoardSlot)) {
            return false;
        }
        MedicalBoardSlot other = (MedicalBoardSlot) object;
        if ((this.getSlotId() == null && other.getSlotId() != null) || (this.getSlotId() != null && !this.getSlotId().equals(other.getSlotId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MedicalBoardSlot[ id=" + this.getSlotId() + " ]";
    }

}
