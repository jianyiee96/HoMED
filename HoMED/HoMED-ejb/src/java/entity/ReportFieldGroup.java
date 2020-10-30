package entity;

import java.io.Serializable;
import javax.persistence.Column;
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

    @Column(nullable = false, length = 128)
    @NotNull(message = "Group Quantity cannot be empty")
    private Integer quantity;

    public ReportFieldGroup() {
    }

    public ReportFieldGroup(String name, Integer quantity) {
        this();
        this.name = name;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
