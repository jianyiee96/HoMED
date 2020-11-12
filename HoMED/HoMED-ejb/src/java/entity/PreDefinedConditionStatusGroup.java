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
import javax.persistence.ManyToMany;

/**
 *
 * @author User
 */
@Entity
public class PreDefinedConditionStatusGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preDefinedConditionStatusGroupId;

    private String groupName;

    @ManyToMany
    List<PreDefinedConditionStatus> statuses;

    public PreDefinedConditionStatusGroup() {
        this.statuses = new ArrayList<>();
    }

    public PreDefinedConditionStatusGroup(String groupName) {
        this();
        this.groupName = groupName;
    }

    public Long getPreDefinedConditionStatusGroupId() {
        return preDefinedConditionStatusGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setPreDefinedConditionStatusGroupId(Long preDefinedConditionStatusGroupId) {
        this.preDefinedConditionStatusGroupId = preDefinedConditionStatusGroupId;
    }

    public List<PreDefinedConditionStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<PreDefinedConditionStatus> statuses) {
        this.statuses = statuses;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (preDefinedConditionStatusGroupId != null ? preDefinedConditionStatusGroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the preDefinedConditionStatusGroupId fields are not set
        if (!(object instanceof PreDefinedConditionStatusGroup)) {
            return false;
        }
        PreDefinedConditionStatusGroup other = (PreDefinedConditionStatusGroup) object;
        if ((this.preDefinedConditionStatusGroupId == null && other.preDefinedConditionStatusGroupId != null) || (this.preDefinedConditionStatusGroupId != null && !this.preDefinedConditionStatusGroupId.equals(other.preDefinedConditionStatusGroupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PreDefinedConditionStatusGroup [ id:" + preDefinedConditionStatusGroupId + " ]";
    }

}
