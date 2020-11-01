/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import util.enumeration.MedicalBoardCaseStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;

@Entity
public class MedicalBoardCase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicalBoardCaseId;

    @Enumerated(EnumType.STRING)
    @Column
    private MedicalBoardTypeEnum medicalBoardType;

    @Enumerated(EnumType.STRING)
    @Column
    private MedicalBoardCaseStatusEnum medicalBoardCaseStatus;

    @OneToOne(optional = false)
    private Consultation consultation;

    @Column(length = 6000)
    private String statementOfCase;

    @Column(nullable = true, length = 6000)
    private String boardFindings;

    @ManyToOne(optional = true)
    private MedicalBoardSlot medicalBoardSlot;

    public MedicalBoardCase() {
        this.medicalBoardCaseStatus = MedicalBoardCaseStatusEnum.WAITING;
    }

    public MedicalBoardCase(Consultation consultation, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) {
        this();
        this.consultation = consultation;
        this.medicalBoardType = medicalBoardType;
        this.statementOfCase = statementOfCase;
    }

    public Long getMedicalBoardCaseId() {
        return medicalBoardCaseId;
    }

    public void setMedicalBoardCaseId(Long medicalBoardCaseId) {
        this.medicalBoardCaseId = medicalBoardCaseId;
    }

    public MedicalBoardTypeEnum getMedicalBoardType() {
        return medicalBoardType;
    }

    public void setMedicalBoardType(MedicalBoardTypeEnum medicalBoardType) {
        this.medicalBoardType = medicalBoardType;
    }

    public MedicalBoardCaseStatusEnum getMedicalBoardCaseStatus() {
        return medicalBoardCaseStatus;
    }

    public void setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum medicalBoardCaseStatus) {
        this.medicalBoardCaseStatus = medicalBoardCaseStatus;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public String getStatementOfCase() {
        return statementOfCase;
    }

    public void setStatementOfCase(String statementOfCase) {
        this.statementOfCase = statementOfCase;
    }

    public String getBoardFindings() {
        return boardFindings;
    }

    public void setBoardFindings(String boardFindings) {
        this.boardFindings = boardFindings;
    }

    public MedicalBoardSlot getMedicalBoardSlot() {
        return medicalBoardSlot;
    }

    public void setMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        if (this.medicalBoardSlot != null) {
            this.medicalBoardSlot.getMedicalBoardCases().remove(this);
        }
        
        this.medicalBoardSlot = medicalBoardSlot;
        
        if (this.medicalBoardSlot != null) {
            if (!this.medicalBoardSlot.getMedicalBoardCases().contains(this)) {
                this.medicalBoardSlot.getMedicalBoardCases().add(this);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (medicalBoardCaseId != null ? medicalBoardCaseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the medicalBoardCaseId fields are not set
        if (!(object instanceof MedicalBoardCase)) {
            return false;
        }
        MedicalBoardCase other = (MedicalBoardCase) object;
        if ((this.medicalBoardCaseId == null && other.medicalBoardCaseId != null) || (this.medicalBoardCaseId != null && !this.medicalBoardCaseId.equals(other.medicalBoardCaseId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MedicalBoardCase [ id: " + medicalBoardCaseId + " ]";
    }

}
