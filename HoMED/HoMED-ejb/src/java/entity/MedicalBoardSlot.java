/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.security.CryptographicHelper;

@Entity
public class MedicalBoardSlot extends Slot implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalBoardSlotStatusEnum medicalBoardSlotStatusEnum;

    private Integer estimatedTimeForEachBoardInPresenceCase;

    private Integer estimatedTimeForEachBoardInAbsenceCase;

    private String medicalOfficerOneKey;

    private String medicalOfficerTwoKey;

    @ManyToOne(optional = true)
    private MedicalOfficer chairman;

    @ManyToOne(optional = true)
    private MedicalOfficer medicalOfficerOne;

    @ManyToOne(optional = true)
    private MedicalOfficer medicalOfficerTwo;

    @OneToMany(mappedBy = "medicalBoardSlot")
    private List<MedicalBoardCase> medicalBoardCases;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date actualStartDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date actualEndDateTime;

    public MedicalBoardSlot() {
        this.medicalBoardSlotStatusEnum = MedicalBoardSlotStatusEnum.UNASSIGNED;
        this.estimatedTimeForEachBoardInPresenceCase = 15;
        this.estimatedTimeForEachBoardInAbsenceCase = 3;
        this.medicalOfficerOneKey = CryptographicHelper.getInstance().generateRandomDigitString(6);
        this.medicalOfficerTwoKey = CryptographicHelper.getInstance().generateRandomDigitString(6);

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

    public Integer getEstimatedTimeForEachBoardInPresenceCase() {
        return estimatedTimeForEachBoardInPresenceCase;
    }

    public void setEstimatedTimeForEachBoardInPresenceCase(Integer estimatedTimeForEachBoardInPresenceCase) {
        this.estimatedTimeForEachBoardInPresenceCase = estimatedTimeForEachBoardInPresenceCase;
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

    public List<MedicalOfficer> getMembersOfBoard() {
        List<MedicalOfficer> membersOfBoard = new ArrayList<>();

        if (this.chairman != null) {
            membersOfBoard.add(chairman);
        }

        if (this.medicalOfficerOne != null) {
            membersOfBoard.add(medicalOfficerOne);
        }

        if (this.medicalOfficerTwo != null) {
            membersOfBoard.add(medicalOfficerTwo);
        }

        return membersOfBoard;

    }

    public List<MedicalBoardCase> getMedicalBoardCases() {
        return medicalBoardCases;
    }

    public void setMedicalBoardCases(List<MedicalBoardCase> medicalBoardCases) {
        this.medicalBoardCases = medicalBoardCases;
    }

    public List<MedicalBoardCase> getMedicalBoardInPresenceCases() {
        return medicalBoardCases.stream().filter(mbs -> mbs.getMedicalBoardType() == MedicalBoardTypeEnum.ABSENCE).collect(toList());
    }

    public List<MedicalBoardCase> getMedicalBoardInAbsenceCases() {
        return medicalBoardCases.stream().filter(mbs -> mbs.getMedicalBoardType() == MedicalBoardTypeEnum.PRESENCE).collect(toList());
    }

    public Integer getTotalEstimatedTime() {
        
        return this.getMedicalBoardInAbsenceCases().size() * this.estimatedTimeForEachBoardInAbsenceCase 
                + this.getMedicalBoardInPresenceCases().size() * this.estimatedTimeForEachBoardInPresenceCase;
        
    }
    
    public String getMedicalOfficerOneKey() {
        return medicalOfficerOneKey;
    }

    public void setMedicalOfficerOneKey(String medicalOfficerOneKey) {
        this.medicalOfficerOneKey = medicalOfficerOneKey;
    }

    public String getMedicalOfficerTwoKey() {
        return medicalOfficerTwoKey;
    }

    public void setMedicalOfficerTwoKey(String medicalOfficerTwoKey) {
        this.medicalOfficerTwoKey = medicalOfficerTwoKey;
    }

    public Date getActualStartDateTime() {
        return actualStartDateTime;
    }

    public void setActualStartDateTime(Date actualStartDateTime) {
        this.actualStartDateTime = actualStartDateTime;
    }

    public Date getActualEndDateTime() {
        return actualEndDateTime;
    }

    public void setActualEndDateTime(Date actualEndDateTime) {
        this.actualEndDateTime = actualEndDateTime;
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
