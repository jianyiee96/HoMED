/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class MedicalBoard implements Serializable {

    @OneToMany
    private List<MedicalBoardSlot> medicalBoardSlots;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicalBoardId;

    public MedicalBoard() {
        this.medicalBoardSlots = new ArrayList<>();
    }

    public Long getMedicalBoardId() {
        return medicalBoardId;
    }

    public void setMedicalBoardId(Long medicalBoardId) {
        this.medicalBoardId = medicalBoardId;
    }

    public List<MedicalBoardSlot> getMedicalBoardSlots() {
        return medicalBoardSlots;
    }

    public void setMedicalBoardSlots(List<MedicalBoardSlot> medicalBoardSlots) {
        this.medicalBoardSlots = medicalBoardSlots;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (medicalBoardId != null ? medicalBoardId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the medicalBoardId fields are not set
        if (!(object instanceof MedicalBoard)) {
            return false;
        }
        MedicalBoard other = (MedicalBoard) object;
        if ((this.medicalBoardId == null && other.medicalBoardId != null) || (this.medicalBoardId != null && !this.medicalBoardId.equals(other.medicalBoardId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MedicalBoard[ id=" + medicalBoardId + " ]";
    }

}
