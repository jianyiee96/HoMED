package jsf.managedBean;

import entity.Report;
import entity.ReportField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import util.enumeration.FilterDateType;
import util.enumeration.ReportDataType;
import util.enumeration.ReportDataValue;
import util.enumeration.ReportFieldType;

@Named(value = "manageReportManagedBean")
@ViewScoped
public class ManageReportManagedBean implements Serializable {

    private Long reportToViewId;
    private String redirectMessage;

    private Boolean isEditMode;
    private Report report;
    private FilterDateType filterDateType;
    private Date filterStartDate;
    private Date filterEndDate;
    private List<Date> filterRangeDates;

    private ReportField reportField;

    private Integer idxAddChart;

    public ManageReportManagedBean() {
        filterRangeDates = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.report = new Report();
        reportField = new ReportField();
        this.idxAddChart = 0;
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("isCreate") != null) {
            isEditMode = true;
        } else {
            try {
                String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reportToViewId");
                if (param != null) {
                    this.reportToViewId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reportToViewId"));
                    // DO CHECK HERE
                    // Ensure that the report that is being viewed is either published/owned by current employee
                    // IF VALID, REFRESH AND RETRIEVE FROM DB
                    // set filterDateType, filterStartDate, filterEndDate
                }
            } catch (NumberFormatException ex) {
                this.report = null;
                redirectMessage = "Invalid Report ID! Redirecting to Report Management.";
//                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "View Report", "Invalid Report ID"));
            }
            isEditMode = false;
        }
    }

    public void addHeader() {
        ReportField rf = new ReportField();
        rf.setType(ReportFieldType.HEADER);
        this.report.getReportFields().add(rf);
    }

    public void doDeleteField(ReportField reportField) {
        if (this.report.getReportFields().contains(reportField)) {
            this.report.getReportFields().remove(reportField);
        }
    }

    public void doOpenDialogFilterDate() {
        this.filterDateType = this.report.getFilterDateType();
        this.filterStartDate = this.report.getFilterStartDate();
        this.filterEndDate = this.report.getFilterEndDate();
        typeFilterDate();
    }

    public void doFilterDate() {
        this.report.setFilterDateType(this.filterDateType);
        this.report.setFilterStartDate(filterStartDate);
        if (filterDateType == filterDateType.CUSTOM || filterDateType == filterDateType.WEEK) {
            this.report.setFilterEndDate(filterEndDate);
        } else {
            this.report.setFilterEndDate(null);
        }
        PrimeFaces.current().executeScript("PF('dialogFilterDate').hide()");
    }

    public void selectFilterDateType() {
        typeFilterDate();
        if (filterStartDate == null || filterEndDate == null) {
            filterRangeDates.clear();
        }
    }

    public void typeFilterDate() {
        if (filterDateType == filterDateType.WEEK) {
            if (filterStartDate != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(filterStartDate);
                int dayOfWeek = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;
                int reduce = dayOfWeek - 1;
                int add = 7 - dayOfWeek;
                c.add(Calendar.DATE, -reduce);
                Date start = c.getTime();
                c.setTime(filterStartDate);
                c.add(Calendar.DATE, add);
                Date end = c.getTime();
                filterStartDate = start;
                filterEndDate = end;
            }
        }
        if (filterDateType == filterDateType.CUSTOM || filterDateType == filterDateType.WEEK) {
            if (filterStartDate != null && filterEndDate != null) {
                if (filterEndDate.before(filterStartDate)) {
                    filterEndDate = filterStartDate;
                }
                filterRangeDates.clear();
                filterRangeDates.add(filterStartDate);
                filterRangeDates.add(filterEndDate);
            }
        }
    }

    public void selectFilterRangeDate() {
        if (filterRangeDates.isEmpty()) {
            return;
        }
        if (filterDateType == filterDateType.CUSTOM) {
            this.filterStartDate = this.filterRangeDates.get(0);
            this.filterEndDate = this.filterRangeDates.get(filterRangeDates.size() - 1);
        } else if (filterDateType == filterDateType.WEEK) {
            this.filterStartDate = this.filterRangeDates.get(0);
            Calendar c = Calendar.getInstance();
            c.setTime(filterStartDate);
            int dayOfWeek = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;
            int reduce = dayOfWeek - 1;
            int add = 7 - dayOfWeek;
            c.add(Calendar.DATE, -reduce);
            Date start = c.getTime();
            c.setTime(filterStartDate);
            c.add(Calendar.DATE, add);
            Date end = c.getTime();
            filterStartDate = start;
            filterEndDate = end;
            filterRangeDates.clear();
            filterRangeDates.add(filterStartDate);
            filterRangeDates.add(filterEndDate);
        }
    }

    public void doOpenDialogAddChart() {
        reportField = new ReportField();
    }

    public void doEdit() {
        isEditMode = !isEditMode;
    }

    public Long getReportToViewId() {
        return reportToViewId;
    }

    public void setReportToViewId(Long reportToViewId) {
        this.reportToViewId = reportToViewId;
    }

    public Boolean getIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(Boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getRedirectMessage() {
        return redirectMessage;
    }

    public FilterDateType[] getFilterDateTypes() {
        return FilterDateType.values();
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

    public Date getToday() {
        return new Date();
    }

    public List<Date> getFilterRangeDates() {
        return filterRangeDates;
    }

    public void setFilterRangeDates(List<Date> filterRangeDates) {
        this.filterRangeDates = filterRangeDates;
    }

    public List<String> getYears() {
        Calendar c = new GregorianCalendar();
        return Stream.iterate(c.get(Calendar.YEAR), x -> x - 1)
                .limit(5)
                .map(x -> String.valueOf(x))
                .collect(Collectors.toList());
    }

    public String getYearRange() {
        Calendar c = new GregorianCalendar();
        return String.valueOf(c.get(Calendar.YEAR) - 5) + ":" + String.valueOf(c.get(Calendar.YEAR));
    }

    public ReportField getReportField() {
        return reportField;
    }

    public void setReportField(ReportField reportField) {
        this.reportField = reportField;
    }

    public Integer getIdxAddChart() {
        return idxAddChart;
    }

    public void setIdxAddChart(Integer idxAddChart) {
        this.idxAddChart = idxAddChart;
    }

    public ReportDataType[] getReportDataTypes() {
        return ReportDataType.values();
    }

    public ReportDataValue[] getReportDataValues() {
        ReportDataType reportDataType = this.reportField.getReportDataType();
        if (reportDataType == null) {
            return ReportDataValue.values();
        } else {
            return ReportDataValue.getReportDataTypeValues(reportDataType);
        }
    }

}
