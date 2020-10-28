package entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.FilterDateType;
import util.enumeration.ReportStatusEnum;

@Entity
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Report Name must be between length 1 to 128")
    @Size(min = 1, max = 128, message = "Report Name must be between length 1 to 128")
    private String name;

    @Column(nullable = true, length = 8000)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date datePublished;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date lastModified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Report Status must be provided")
    private ReportStatusEnum reportStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilterDateType filterDateType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date filterStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date filterEndDate;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReportField> reportFields;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Employee employee;

    public Report() {
        this.filterDateType = FilterDateType.NONE;
        this.dateCreated = new Date();
        this.lastModified = new Date();
        this.reportStatus = ReportStatusEnum.PERSONAL;
        this.reportFields = new ArrayList<>();
    }

    public Report(String name, String description, Date filterStartDate, Date filterEndDate) {
        this();
        this.name = name;
        this.description = description;
        this.filterStartDate = filterStartDate;
        this.filterEndDate = filterEndDate;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public ReportStatusEnum getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatusEnum reportStatus) {
        this.reportStatus = reportStatus;
    }

    public FilterDateType getFilterDateType() {
        return filterDateType;
    }

    public void setFilterDateType(FilterDateType filterDateType) {
        this.filterDateType = filterDateType;
    }

    public Date getFilterStartDate() {
        return filterStartDate;
    }

    public void setFilterStartDate(Date filterStartDate) {
        this.filterStartDate = filterStartDate;
    }

    public Date getFilterEndDate() {
        return filterEndDate;
    }

    public void setFilterEndDate(Date filterEndDate) {
        this.filterEndDate = filterEndDate;
    }

    public List<ReportField> getReportFields() {
        return reportFields;
    }

    public void setReportFields(List<ReportField> reportFields) {
        this.reportFields = reportFields;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getFilterDateString() {
        SimpleDateFormat day = new SimpleDateFormat("dd MMM yy");
        SimpleDateFormat month = new SimpleDateFormat("MMMMM yyyy");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        String result = "";
        if (filterDateType == FilterDateType.DAY) {
            result = day.format(filterStartDate);
        } else if (filterDateType == FilterDateType.WEEK || filterDateType == FilterDateType.CUSTOM) {
            result = day.format(filterStartDate);
            result += " - " + day.format(filterEndDate);
        } else if (filterDateType == FilterDateType.MONTH) {
            result = month.format(filterStartDate);
        } else if (filterDateType == FilterDateType.YEAR) {
            result = year.format(filterStartDate);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportId != null ? reportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reportId fields are not set
        if (!(object instanceof Report)) {
            return false;
        }
        Report other = (Report) object;
        if ((this.reportId == null && other.reportId != null) || (this.reportId != null && !this.reportId.equals(other.reportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + " [id:" + reportId + "]";
    }

}
