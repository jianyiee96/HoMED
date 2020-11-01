/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.MedicalBoardSlotStatusEnum;

@Entity
public class MedicalBoardSlot extends Slot implements Serializable {

    private MedicalBoardSlotStatusEnum medicalBoardSlotStatusEnum;

    private Integer numBoardInPresenceCases;
    private Integer estimatedTimeForEachBoardInPresenceCase;

    private Integer numBoardInAbsenceCases;
    private Integer estimatedTimeForEachBoardInAbsenceCase;

    @ManyToOne(optional = true)
    private MedicalOfficer chairman;

    @ManyToOne(optional = true)
    private MedicalOfficer medicalOfficerOne;

    @ManyToOne(optional = true)
    private MedicalOfficer medicalOfficerTwo;

    @OneToMany(mappedBy = "medicalBoardSlot")
    private List<MedicalBoardCase> medicalBoardCases;

    public MedicalBoardSlot() {
        this.medicalBoardSlotStatusEnum = MedicalBoardSlotStatusEnum.UNALLOCATED;
        this.numBoardInPresenceCases = 0;
        this.estimatedTimeForEachBoardInPresenceCase = 15;
        this.numBoardInAbsenceCases = 0;
        this.estimatedTimeForEachBoardInAbsenceCase = 3;
        
        this.medicalBoardCases = new ArrayList<>();
    }

    public MedicalBoardSlot(Date start, Date end) {
        this();
        super.setStartDateTime(start);
        super.setEndDateTime(end);
    }

    public MedicalBoardSlotStatusEnum getMedicalBoardSlotStatusEnum() {
        return medicalBoardSlotStatusEnum;
    }

    public void setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum medicalBoardSlotStatusEnum) {
        this.medicalBoardSlotStatusEnum = medicalBoardSlotStatusEnum;
    }

    public Integer getNumBoardInPresenceCases() {
        return numBoardInPresenceCases;
    }

    public void setNumBoardInPresenceCases(Integer numBoardInPresenceCases) {
        this.numBoardInPresenceCases = numBoardInPresenceCases;
    }

    public Integer getEstimatedTimeForEachBoardInPresenceCase() {
        return estimatedTimeForEachBoardInPresenceCase;
    }

    public void setEstimatedTimeForEachBoardInPresenceCase(Integer estimatedTimeForEachBoardInPresenceCase) {
        this.estimatedTimeForEachBoardInPresenceCase = estimatedTimeForEachBoardInPresenceCase;
    }

    public Integer getNumBoardInAbsenceCases() {
        return numBoardInAbsenceCases;
    }

    public void setNumBoardInAbsenceCases(Integer numBoardInAbsenceCases) {
        this.numBoardInAbsenceCases = numBoardInAbsenceCases;
    }

    public Integer getEstimatedTimeForEachBoardInAbsenceCase() {
        return estimatedTimeForEachBoardInAbsenceCase;
    }

    public void setEstimatedTimeForEachBoardInAbsenceCase(Integer estimatedTimeForEachBoardInAbsenceCase) {
        this.estimatedTimeForEachBoardInAbsenceCase = estimatedTimeForEachBoardInAbsenceCase;
    }

    public MedicalOfficer getChairman() {
        return chairman;
    }

    public void setChairman(MedicalOfficer chairman) {
        if (this.chairman != null) {
            this.chairman.getMedicalBoardSlotsAsMedicalOfficerOne().remove(this);
        }

        this.chairman = chairman;

        if (this.chairman != null) {
            if (!this.chairman.getMedicalBoardSlotsAsMedicalOfficerOne().contains(this)) {
                this.chairman.getMedicalBoardSlotsAsMedicalOfficerOne().add(this);
            }
        }
    }

    public MedicalOfficer getMedicalOfficerOne() {
        return medicalOfficerOne;
    }

    public void setMedicalOfficerOne(MedicalOfficer medicalOfficerOne) {
        if (this.medicalOfficerOne != null) {
            this.medicalOfficerOne.getMedicalBoardSlotsAsMedicalOfficerOne().remove(this);
        }

        this.medicalOfficerOne = medicalOfficerOne;

        if (this.medicalOfficerOne != null) {
            if (!this.medicalOfficerOne.getMedicalBoardSlotsAsMedicalOfficerOne().contains(this)) {
                this.medicalOfficerOne.getMedicalBoardSlotsAsMedicalOfficerOne().add(this);
            }
        }
    }

    public MedicalOfficer getMedicalOfficerTwo() {
        return medicalOfficerTwo;
    }

    public void setMedicalOfficerTwo(MedicalOfficer medicalOfficerTwo) {
        if (this.medicalOfficerTwo != null) {
            this.medicalOfficerTwo.getMedicalBoardSlotsAsMedicalOfficerOne().remove(this);
        }

        this.medicalOfficerTwo = medicalOfficerTwo;

        if (this.medicalOfficerTwo != null) {
            if (!this.medicalOfficerTwo.getMedicalBoardSlotsAsMedicalOfficerOne().contains(this)) {
                this.medicalOfficerTwo.getMedicalBoardSlotsAsMedicalOfficerOne().add(this);
            }
        }
    }

    public List<MedicalBoardCase> getMedicalBoardCases() {
        return medicalBoardCases;
    }

    public void setMedicalBoardCases(List<MedicalBoardCase> medicalBoardCases) {
        this.medicalBoardCases = medicalBoardCases;
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
        return "Medical Board Slot [ id: " + this.getSlotId() + " ]";
    }

}
