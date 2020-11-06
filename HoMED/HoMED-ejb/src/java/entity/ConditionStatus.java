/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author User
 */
@Entity
public class ConditionStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conditionStatusId;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    Serviceman serviceman;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    MedicalBoardCase medicalBoardCase;

    @Column(nullable = false)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date statusEndDate;
    
    private Boolean isActive;

    public ConditionStatus(){
        this.isActive = Boolean.TRUE;
    }
    
    public Long getConditionStatusId() {
        return conditionStatusId;
    }

    public void setConditionStatusId(Long conditionStatusId) {
        this.conditionStatusId = conditionStatusId;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStatusEndDate() {
        return statusEndDate;
    }

    public void setStatusEndDate(Date statusEndDate) {
        this.statusEndDate = statusEndDate;
    }

    public MedicalBoardCase getMedicalBoardCase() {
        return medicalBoardCase;
    }

    public void setMedicalBoardCase(MedicalBoardCase medicalBoardCase) {
        this.medicalBoardCase = medicalBoardCase;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conditionStatusId != null ? conditionStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the conditionStatusId fields are not set
        if (!(object instanceof ConditionStatus)) {
            return false;
        }
        ConditionStatus other = (ConditionStatus) object;
        if ((this.conditionStatusId == null && other.conditionStatusId != null) || (this.conditionStatusId != null && !this.conditionStatusId.equals(other.conditionStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ConditionStatus[ id=" + conditionStatusId + " ]";
    }

}
