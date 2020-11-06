/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author User
 */
@Entity
public class PreDefinedConditionStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preDefinedConditionStatusId;

    String description;

    public PreDefinedConditionStatus() {
    }

    public PreDefinedConditionStatus(String description) {
        this.description = description;
    }
    
    public Long getPreDefinedConditionStatusId() {
        return preDefinedConditionStatusId;
    }

    public void setPreDefinedConditionStatusId(Long preDefinedConditionStatusId) {
        this.preDefinedConditionStatusId = preDefinedConditionStatusId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (preDefinedConditionStatusId != null ? preDefinedConditionStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the preDefinedConditionStatusId fields are not set
        if (!(object instanceof PreDefinedConditionStatus)) {
            return false;
        }
        PreDefinedConditionStatus other = (PreDefinedConditionStatus) object;
        if ((this.preDefinedConditionStatusId == null && other.preDefinedConditionStatusId != null) || (this.preDefinedConditionStatusId != null && !this.preDefinedConditionStatusId.equals(other.preDefinedConditionStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PreDefinedConditionStatus[ id:" + preDefinedConditionStatusId + " ]";
    }
    
}
