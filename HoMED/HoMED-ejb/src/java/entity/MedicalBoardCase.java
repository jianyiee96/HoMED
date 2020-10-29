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
import javax.persistence.OneToOne;
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

    @OneToOne(optional = false)
    private Consultation consultation;
    
    @OneToOne(optional = false)
    private FormInstance medicalBoardForm;

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

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public FormInstance getMedicalBoardForm() {
        return medicalBoardForm;
    }

    public void setMedicalBoardForm(FormInstance medicalBoardForm) {
        this.medicalBoardForm = medicalBoardForm;
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
        return "entity.MedicalBoardCase[ id=" + medicalBoardCaseId + " ]";
    }

}
