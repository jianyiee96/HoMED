package entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.FilterDateType;
import util.enumeration.ReportDataGrouping;
import util.enumeration.ReportDataType;
import util.enumeration.ReportDataValue;
import util.enumeration.ReportFieldType;

@Entity
public class ReportField implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportFieldId;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Report Field Name must be between length 1 to 128")
    @Size(min = 1, max = 128, message = "Report Field Name must be between length 1 to 128")
    private String name;

    @Column(nullable = true, length = 8000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Report Field Type must be provided")
    private ReportFieldType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilterDateType filterDateType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date filterStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date filterEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ReportDataType reportDataType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ReportDataValue reportDataValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ReportDataGrouping reportDataGrouping;

    @Column(nullable = true)
    private String x_axis;

    @Column(nullable = true)
    private String y_axis;

    @Column(nullable = true)
    private String aggregateString;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReportFieldGroup> reportFieldGroups;

    // FOR LINE CHARTS ONLY
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReportField> datasetFields;

    public ReportField() {
        this.filterDateType = FilterDateType.NONE;
        this.reportFieldGroups = new ArrayList<>();
        this.datasetFields = new ArrayList<>();
    }

    public ReportField(String name, String description, ReportFieldType type, Date filterStartDate, Date filterEndDate, List<ReportFieldGroup> reportFieldGroups) {
        this();
        this.name = name;
        this.description = description;
        this.type = type;
        this.filterStartDate = filterStartDate;
        this.filterEndDate = filterEndDate;
        this.reportFieldGroups = reportFieldGroups;
    }

    public ReportField(ReportField another) {
        this();
        this.reportFieldId = another.getReportFieldId();
        this.name = another.name;
        this.description = another.description;
        this.type = another.type;
        this.filterDateType = another.getFilterDateType();
        this.filterStartDate = another.filterStartDate != null ? new Date(another.filterStartDate.getTime()) : null;
        this.filterEndDate = another.filterEndDate != null ? new Date(another.filterEndDate.getTime()) : null;
        this.reportDataType = another.getReportDataType();
        this.reportDataValue = another.getReportDataValue();
        this.reportDataGrouping = another.getReportDataGrouping();
        this.x_axis = another.x_axis;
        this.y_axis = another.y_axis;
        this.aggregateString = another.aggregateString;
        this.reportFieldGroups = another.reportFieldGroups.stream()
                .map(grp -> {
                    ReportFieldGroup newGroup = new ReportFieldGroup();
                    newGroup.setReportFieldGroupId(grp.getReportFieldGroupId());
                    newGroup.setName(grp.getName());
                    newGroup.setQuantity(grp.getQuantity());
                    return newGroup;
                })
                .collect(Collectors.toList());
        this.datasetFields = another.datasetFields.stream()
                .map(field -> new ReportField(field))
                .collect(Collectors.toList());
    }

    public Long getReportFieldId() {
        return reportFieldId;
    }

    public void setReportFieldId(Long reportFieldId) {
        this.reportFieldId = reportFieldId;
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

    public ReportFieldType getType() {
        return type;
    }

    public void setType(ReportFieldType type) {
        this.type = type;
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

    public List<ReportFieldGroup> getReportFieldGroups() {
        return reportFieldGroups;
    }

    public void setReportFieldGroups(List<ReportFieldGroup> reportFieldGroups) {
        this.reportFieldGroups = reportFieldGroups;
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

    public FilterDateType getFilterDateType() {
        return filterDateType;
    }

    public void setFilterDateType(FilterDateType filterDateType) {
        this.filterDateType = filterDateType;
    }

    public ReportDataType getReportDataType() {
        return reportDataType;
    }

    public void setReportDataType(ReportDataType reportDataType) {
        this.reportDataType = reportDataType;
    }

    public ReportDataValue getReportDataValue() {
        return reportDataValue;
    }

    public void setReportDataValue(ReportDataValue reportDataValue) {
        this.reportDataValue = reportDataValue;
    }

    public ReportDataGrouping getReportDataGrouping() {
        return reportDataGrouping;
    }

    public void setReportDataGrouping(ReportDataGrouping reportDataGrouping) {
        this.reportDataGrouping = reportDataGrouping;
    }

    public List<ReportField> getDatasetFields() {
        return datasetFields;
    }

    public void setDatasetFields(List<ReportField> datasetFields) {
        this.datasetFields = datasetFields;
    }

    public String getX_axis() {
        return x_axis;
    }

    public void setX_axis(String x_axis) {
        this.x_axis = x_axis;
    }

    public String getY_axis() {
        return y_axis;
    }

    public void setY_axis(String y_axis) {
        this.y_axis = y_axis;
    }

    public String getAggregateString() {
        return aggregateString;
    }

    public void setAggregateString(String aggregateString) {
        this.aggregateString = aggregateString;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportFieldId != null ? reportFieldId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reportFieldId fields are not set
        if (!(object instanceof ReportField)) {
            return false;
        }
        ReportField other = (ReportField) object;
        if ((this.reportFieldId == null && other.reportFieldId != null) || (this.reportFieldId != null && !this.reportFieldId.equals(other.reportFieldId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReportField [ id:" + reportFieldId + " ]";
    }

}
