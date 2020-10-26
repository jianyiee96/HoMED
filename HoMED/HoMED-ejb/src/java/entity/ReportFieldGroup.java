package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class ReportFieldGroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportFieldValueGroupId;

    @Column(nullable = true, length = 128)
    @NotNull(message = "Group Name must be between length 1 to 128")
    @Size(min = 1, max = 128, message = "Group Name must be between length 1 to 128")
    private String name;

    @ElementCollection
    private List<String> groupValues;

    public ReportFieldGroup() {
        this.groupValues = new ArrayList();
    }

    public ReportFieldGroup(String name, List<String> groupValues) {
        this();
        this.name = name;
        this.groupValues = groupValues;
    }

    public List<String> getGroupValues() {
        return groupValues;
    }

    public void setGroupValues(List<String> groupValues) {
        this.groupValues = groupValues;
    }

    public Long getReportFieldValueGroupId() {
        return reportFieldValueGroupId;
    }

    public void setReportFieldValueGroupId(Long reportFieldValueGroupId) {
        this.reportFieldValueGroupId = reportFieldValueGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportFieldValueGroupId != null ? reportFieldValueGroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reportFieldValueGroupId fields are not set
        if (!(object instanceof ReportFieldGroup)) {
            return false;
        }
        ReportFieldGroup other = (ReportFieldGroup) object;
        if ((this.reportFieldValueGroupId == null && other.reportFieldValueGroupId != null) || (this.reportFieldValueGroupId != null && !this.reportFieldValueGroupId.equals(other.reportFieldValueGroupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReportFieldValueGroup [ id: " + reportFieldValueGroupId + " ]";
    }

}
