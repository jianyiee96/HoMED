package jsf.managedBean;

import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Consultation;
import entity.MedicalOfficer;
import entity.Report;
import entity.ReportField;
import entity.ReportFieldGroup;
import entity.Serviceman;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScaleLabel;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import util.enumeration.BookingStatusEnum;
import util.enumeration.ConsultationStatusEnum;
import util.enumeration.FilterDateType;
import util.enumeration.ReportDataGrouping;
import util.enumeration.ReportDataType;
import util.enumeration.ReportDataValue;
import util.enumeration.ReportFieldType;

@Named(value = "manageReportManagedBean")
@ViewScoped
public class ManageReportManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private Long reportToViewId;
    private String redirectMessage;

    private Boolean isEditMode;
    private Report report;
    private FilterDateType filterDateType;
    private Date filterStartDate;
    private Date filterEndDate;
    private List<Date> filterRangeDates;

    private ReportField reportField;

    private String dialogHeaderAddChart;
    private Integer idxAddChart;

    private List<Serviceman> servicemenData;
    private List<Consultation> consultationsData;
    private List<MedicalOfficer> medicalOfficersData;

    private PieChartModel pieModel;
    private BarChartModel barModel;

    List<String> bgColors;
    List<String> borderColors;

    public ManageReportManagedBean() {
        filterRangeDates = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        this.pieModel = new PieChartModel();
        this.barModel = new BarChartModel();
        refreshData();
        setColours();
        this.report = new Report();
        this.reportField = new ReportField();
        this.dialogHeaderAddChart = "";
        this.idxAddChart = 0;
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("isCreate") != null) {
            this.isEditMode = true;
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
                this.redirectMessage = "Invalid Report ID! Redirecting to Report Management.";
//                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "View Report", "Invalid Report ID"));
            }
            this.isEditMode = false;
        }
    }

    private void refreshData() {
        this.servicemenData = servicemanSessionBeanLocal.retrieveAllServicemen();
        this.consultationsData = consultationSessionBeanLocal.retrieveAllConsultations();
        this.medicalOfficersData = employeeSessionBeanLocal.retrieveAllMedicalOfficers();
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
        this.idxAddChart = 0;
        this.dialogHeaderAddChart = "Add Chart";
        reportField = new ReportField();
        reportField.setReportDataType(ReportDataType.SERVICEMAN);
        reportField.setReportDataValue(ReportDataValue.QTY);
        reportField.setReportDataGrouping(ReportDataGrouping.S_BT);

    }

    public void doDialogAddChartNextPage() {
        this.idxAddChart += 1;
        if (idxAddChart == 1) {
            if (checkAddChartDate()) {
                this.filterDateType = this.reportField.getFilterDateType();
                this.filterStartDate = this.reportField.getFilterStartDate();
                this.filterEndDate = this.reportField.getFilterEndDate();
                typeFilterDate();
            }
        } else if (idxAddChart == 2) {
            if (checkAddChartDate()) {
                if (this.report.getFilterDateType() == FilterDateType.NONE) {
                    this.reportField.setFilterDateType(this.filterDateType);
                    this.reportField.setFilterStartDate(filterStartDate);
                    if (filterDateType == filterDateType.CUSTOM || filterDateType == filterDateType.WEEK) {
                        this.reportField.setFilterEndDate(filterEndDate);
                    } else {
                        this.reportField.setFilterEndDate(null);
                    }
                } else {
                    this.reportField.setFilterDateType(this.report.getFilterDateType());
                    this.reportField.setFilterStartDate(this.report.getFilterStartDate());
                    this.reportField.setFilterEndDate(this.report.getFilterEndDate());
                }
            }
        } else if (idxAddChart == 3) {
            processField(this.reportField);
            createChart(this.reportField);
        }
    }

    public Boolean checkAddChartDate() {
        return this.reportField.getReportDataGrouping() == ReportDataGrouping.S_BK
                || this.reportField.getReportDataGrouping() == ReportDataGrouping.MO_CS
                || this.reportField.getReportDataGrouping() == ReportDataGrouping.MO_FI;
    }

    private void processField(ReportField reportField) {
        ReportDataType dataType = reportField.getReportDataType();
        ReportDataValue dataValue = reportField.getReportDataValue();
        ReportDataGrouping dataGrouping = reportField.getReportDataGrouping();
        FilterDateType filterDateType = reportField.getFilterDateType();
        Date startDate = reportField.getFilterStartDate();
        Date endDate = reportField.getFilterEndDate();
        HashMap<String, Integer> freqMap = new HashMap<>();
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        if (dataGrouping == ReportDataGrouping.S_BT) {
            this.servicemenData.stream()
                    .map(s -> s.getBloodType().getBloodTypeString())
                    .forEach(bt -> {
                        int count = freqMap.containsKey(bt) ? freqMap.get(bt) : 0;
                        freqMap.put(bt, count + 1);
                    });
            sortedResultList = sortByType(freqMap, "STRING");
        } else if (dataGrouping == ReportDataGrouping.S_PES) {

        } else if (dataGrouping == ReportDataGrouping.S_BK) {
            HashMap<Integer, Integer> preFreqMap = new HashMap<>();
            this.servicemenData.stream()
                    .map(s -> (int) s.getBookings().stream()
                    .filter(b -> {
                        return b.getBookingStatusEnum() != BookingStatusEnum.ABSENT
                                && b.getBookingStatusEnum() != BookingStatusEnum.CANCELLED
                                && isWithinRange(filterDateType, startDate, endDate, b.getBookingSlot().getStartDateTime());
                    })
                    .count())
                    .forEach(counter -> {
                        int count = preFreqMap.containsKey(counter) ? preFreqMap.get(counter) : 0;
                        preFreqMap.put(counter, count + 1);
                    });
            if (!preFreqMap.isEmpty()) {
                int limit = (int) Math.ceil(preFreqMap.keySet().stream().max(Integer::compare).get() / 5.0) * 5;
                for (int i = 0; i < limit; i += 5) {
                    int min = i == 0 ? i : i + 1;
                    int max = i + 5;
                    Integer count = preFreqMap.entrySet().stream().filter(entry -> entry.getKey() >= min && entry.getKey() <= max).map(entry -> entry.getValue()).reduce(0, (x, y) -> x + y);
                    String str = String.valueOf(min) + " - " + String.valueOf(max);
                    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<String, Integer>(str, count);
                    sortedResultList.add(entry);
                }
            }
        } else if (dataGrouping == ReportDataGrouping.S_BK) {

        } else if (dataGrouping == ReportDataGrouping.MO_CS) {
            HashMap<Integer, Integer> preFreqMap = new HashMap<>();
            this.medicalOfficersData.stream()
                    .map(mo -> (int) mo.getCompletedConsultations().stream().filter(c -> isWithinRange(filterDateType, startDate, endDate, c.getStartDateTime())).count())
                    .forEach(counter -> {
                        int count = preFreqMap.containsKey(counter) ? preFreqMap.get(counter) : 0;
                        preFreqMap.put(counter, count + 1);
                    });
            if (!preFreqMap.isEmpty()) {
                int limit = (int) Math.ceil(preFreqMap.keySet().stream().max(Long::compare).get() / 5.0) * 5;
                for (int i = 0; i < limit; i += 5) {
                    int min = i == 0 ? i : i + 1;
                    int max = i + 5;
                    Integer count = preFreqMap.entrySet().stream().filter(entry -> entry.getKey() >= min && entry.getKey() <= max).map(entry -> entry.getValue()).reduce(0, (x, y) -> x + y);
                    String str = String.valueOf(min) + " - " + String.valueOf(max);
                    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<String, Integer>(str, count);
                    sortedResultList.add(entry);
                }
            }
        } else if (dataGrouping == ReportDataGrouping.MO_FI) {
            HashMap<Integer, Integer> preFreqMap = new HashMap<>();
            this.medicalOfficersData.stream()
                    .map(mo -> (int) mo.getSignedFormInstances().stream().filter(fi -> isWithinRange(filterDateType, startDate, endDate, fi.getDateSubmitted())).count())
                    .forEach(counter -> {
                        int count = preFreqMap.containsKey(counter) ? preFreqMap.get(counter) : 0;
                        preFreqMap.put(counter, count + 1);
                    });
            if (!preFreqMap.isEmpty()) {
                int limit = (int) Math.ceil(preFreqMap.keySet().stream().max(Long::compare).get() / 5.0) * 5;
                for (int i = 0; i < limit; i += 5) {
                    int min = i == 0 ? i : i + 1;
                    int max = i + 5;
                    Integer count = preFreqMap.entrySet().stream().filter(entry -> entry.getKey() >= min && entry.getKey() <= max).map(entry -> entry.getValue()).reduce(0, (x, y) -> x + y);
                    String str = String.valueOf(min) + " - " + String.valueOf(max);
                    Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<String, Integer>(str, count);
                    sortedResultList.add(entry);
                }
            }
        } else if (dataGrouping == ReportDataGrouping.MO_MC) {
            this.medicalOfficersData.stream()
                    .filter(mo -> mo.getMedicalCentre() != null)
                    .map(mo -> mo.getMedicalCentre().getName())
                    .forEach(mc -> {
                        int count = freqMap.containsKey(mc) ? freqMap.get(mc) : 0;
                        freqMap.put(mc, count + 1);
                    });
            sortedResultList = sortByType(freqMap, "STRING");
        }
        List<ReportFieldGroup> groups = reportField.getReportFieldGroups();
        groups.clear();

        sortedResultList.forEach((mapEntry) -> {
            groups.add(new ReportFieldGroup(mapEntry.getKey(), mapEntry.getValue()));
        });
    }

    public List<Map.Entry<String, Integer>> sortByType(HashMap<String, Integer> map, String type) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        if (type.equals("STRING")) {
            list = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(String::toString)))
                    .collect(Collectors.toList());
        }
        return list;
    }

    private void createChart(ReportField reportField) {
        if (reportField.getType() == ReportFieldType.PIE) {
            createPieModel(reportField.getReportFieldGroups());
        } else if (reportField.getType() == ReportFieldType.BAR) {
            createBarModel(reportField.getReportFieldGroups());
        } else if (reportField.getType() == ReportFieldType.LINE) {

        }
    }

    private void createPieModel(List<ReportFieldGroup> groups) {
        this.pieModel = new PieChartModel();
        ChartData data = new ChartData();

        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> borderColors = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getQuantity() > 0) {
                values.add(groups.get(i).getQuantity());
                labels.add(groups.get(i).getName());
                borderColors.add(this.borderColors.get(i % this.borderColors.size()));
            }
        }
        dataSet.setData(values);

        dataSet.setBackgroundColor(borderColors);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        this.pieModel.setData(data);
    }

    public void createBarModel(List<ReportFieldGroup> groups) {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Dataset");

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> borderColors = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            values.add(groups.get(i).getQuantity());
            labels.add(groups.get(i).getName());
            bgColors.add(this.bgColors.get(i % this.bgColors.size()));
            borderColors.add(this.borderColors.get(i % this.borderColors.size()));
        }
        barDataSet.setData(values);
        barDataSet.setBackgroundColor(bgColors);
        barDataSet.setBorderColor(borderColors);
        barDataSet.setBorderWidth(1);
        data.addChartDataSet(barDataSet);
        data.setLabels(labels);
        barModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setStepSize(1);
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        CartesianScaleLabel label = new CartesianScaleLabel();
        label.setLabelString("TEST");
        linearAxes.setScaleLabel(label);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        barModel.setOptions(options);
    }

    public void setColours() {
        bgColors = new ArrayList<>();
        borderColors = new ArrayList<>();
        bgColors.add("rgba(255, 99, 132, 0.2)");
        bgColors.add("rgba(255, 159, 64, 0.2)");
        bgColors.add("rgba(255, 205, 86, 0.2)");
        bgColors.add("rgba(75, 192, 192, 0.2)");
        bgColors.add("rgba(54, 162, 235, 0.2)");
        bgColors.add("rgba(153, 102, 255, 0.2)");
        bgColors.add("rgba(201, 203, 207, 0.2)");

        borderColors.add("rgb(255, 99, 132)");
        borderColors.add("rgb(255, 159, 64)");
        borderColors.add("rgb(255, 205, 86)");
        borderColors.add("rgb(75, 192, 192)");
        borderColors.add("rgb(54, 162, 235)");
        borderColors.add("rgb(153, 102, 255)");
        borderColors.add("rgb(201, 203, 207)");
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

    public Boolean isWithinRange(FilterDateType type, Date start, Date end, Date check) {
        if (type == FilterDateType.NONE) {
            return true;
        } else if (type == FilterDateType.YEAR) {
            return start.getYear() == check.getYear();
        } else if (type == FilterDateType.MONTH) {
            return start.getYear() == check.getYear() && start.getMonth() == check.getMonth();
        } else if (type == FilterDateType.DAY) {
            return start.getYear() == check.getYear() && start.getMonth() == check.getMonth() && start.getDate() == check.getDate();
        } else if (type == FilterDateType.WEEK || type == FilterDateType.CUSTOM) {
            return getFloorDay(start).before(check) && getCeilingDay(end).after(check);
        }
        return true;
    }

    public Date getFloorDay(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Date getCeilingDay(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
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

    public ReportDataGrouping[] getReportDataGroupings() {
        ReportDataType reportDataType = this.reportField.getReportDataType();
        ReportDataValue reportDataValue = this.reportField.getReportDataValue();
        if (reportDataType == null) {
            return null;
        } else {
            return ReportDataGrouping.getReportDataGroupings(reportDataType, reportDataValue);
        }
    }

    public ReportFieldType[] getReportFieldTypes() {
        ReportDataGrouping reportDataGrouping = this.reportField.getReportDataGrouping();
        if (reportDataGrouping == null) {
            return ReportFieldType.values();
        } else {
            return ReportFieldType.getReportFieldTypes(reportDataGrouping);
        }
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public String getDialogHeaderAddChart() {
        return dialogHeaderAddChart;
    }

}
