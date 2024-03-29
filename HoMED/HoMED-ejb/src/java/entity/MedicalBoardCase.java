/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import util.enumeration.MedicalBoardCaseStatusEnum;
import util.enumeration.MedicalBoardTypeEnum;
import util.enumeration.PesStatusEnum;

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

    @OneToOne(optional = true)
    private Consultation consultation;

    @Column(length = 6000)
    private String statementOfCase;

    @Column(nullable = true, length = 6000)
    private String boardFindings;

    @ManyToOne(optional = true)
    private MedicalBoardSlot medicalBoardSlot;

    @OneToOne(optional = true)
    private MedicalBoardCase previousMedicalBoardCase;

    @OneToOne(optional = true, mappedBy = "previousMedicalBoardCase")
    private MedicalBoardCase followUpMedicalBoardCase;

    private Boolean isSigned;

    @Enumerated(EnumType.STRING)
    @Column
    private PesStatusEnum finalPesStatus;

    @OneToMany
    private List<ConditionStatus> conditionStatuses;

    public MedicalBoardCase() {
        this.medicalBoardCaseStatus = MedicalBoardCaseStatusEnum.WAITING;
        this.isSigned = Boolean.FALSE;
        this.conditionStatuses = new ArrayList<>();
    }

    public MedicalBoardCase(Consultation consultation, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) {
        this();
        this.consultation = consultation;
        this.medicalBoardType = medicalBoardType;
        this.statementOfCase = statementOfCase;
    }

    public MedicalBoardCase(MedicalBoardCase previousMedicalBoardCase, MedicalBoardTypeEnum medicalBoardType, String statementOfCase) {
        this();
        this.previousMedicalBoardCase = previousMedicalBoardCase;
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
        try {
            return consultationRec(this);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Consultation getConsultationStrict() {
        return this.consultation;
    }

    private Consultation consultationRec(MedicalBoardCase medicalBoardCase) {
        if (medicalBoardCase.getConsultationStrict() != null) {
            return medicalBoardCase.getConsultationStrict();
        } else {
            medicalBoardCase = medicalBoardCase.getPreviousMedicalBoardCase();
            return consultationRec(medicalBoardCase);
        }
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
        this.medicalBoardSlot = medicalBoardSlot;
    }

    public MedicalBoardCase getPreviousMedicalBoardCase() {
        return previousMedicalBoardCase;
    }

    public void setPreviousMedicalBoardCase(MedicalBoardCase previousMedicalBoardCase) {
        this.previousMedicalBoardCase = previousMedicalBoardCase;
    }

    public MedicalBoardCase getFollowUpMedicalBoardCase() {
        return followUpMedicalBoardCase;
    }

    public void setFollowUpMedicalBoardCase(MedicalBoardCase followUpMedicalBoardCase) {
        this.followUpMedicalBoardCase = followUpMedicalBoardCase;
    }

    public Boolean getIsSigned() {
        return isSigned;
    }

    public void setIsSigned(Boolean isSigned) {
        this.isSigned = isSigned;
    }

    public PesStatusEnum getFinalPesStatus() {
        return finalPesStatus;
    }

    public void setFinalPesStatus(PesStatusEnum finalPesStatus) {
        this.finalPesStatus = finalPesStatus;
    }

    public List<ConditionStatus> getConditionStatuses() {
        return conditionStatuses;
    }

    public void setConditionStatuses(List<ConditionStatus> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
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
