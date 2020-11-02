package jsf.managedBean;

import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ReportSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Consultation;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalOfficer;
import entity.Report;
import entity.ReportField;
import entity.ReportFieldGroup;
import entity.Serviceman;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import jsf.classes.ReportFieldWrapper;
import org.primefaces.PrimeFaces;
import util.enumeration.BookingStatusEnum;
import util.enumeration.ConsultationStatusEnum;
import util.enumeration.FilterDateType;
import util.enumeration.ReportDataGrouping;
import util.enumeration.ReportDataType;
import util.enumeration.ReportDataValue;
import util.enumeration.ReportFieldType;
import util.enumeration.ReportNotFoundException;
import util.exceptions.CloneReportException;
import util.exceptions.CreateReportException;
import util.exceptions.DeleteReportException;
import util.exceptions.PublishReportException;
import util.exceptions.UpdateReportException;

@Named(value = "manageReportManagedBean")
@ViewScoped
public class ManageReportManagedBean implements Serializable {

    @EJB(name = "ReportSessionBeanLocal")
    private ReportSessionBeanLocal reportSessionBeanLocal;

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    private Employee currentEmployee;
    private String redirectMessage;

    // To detect new report creation (AKA when url param is empty)
    private Boolean isCreateState;
    // To detect view only (for viewing other employees published reports)
    private Boolean isViewState;
    private Boolean isEditMode;
    private Report report;
    private FilterDateType filterDateType;
    private Date filterStartDate;
    private Date filterEndDate;
    private List<Date> filterRangeDates;

    private List<ReportFieldWrapper> reportFieldWrappers;

    // Either ADD/EDIT Mode
    private Boolean isAddMode;
    private ReportFieldWrapper reportFieldToAddWrapper;
    private Integer reportFieldToEditWrapperIdx;

    private String dialogHeaderAddChart;
    private Integer idxAddChart;

    private List<Serviceman> servicemenData;
    private List<MedicalOfficer> medicalOfficersData;
    private List<Consultation> consultationsData;

    public ManageReportManagedBean() {
        filterRangeDates = new ArrayList<>();
        this.reportFieldWrappers = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        refreshData();
        this.report = new Report();
        this.dialogHeaderAddChart = "";
        this.isAddMode = true;
        this.idxAddChart = 0;

        currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        try {
            String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reportToViewId");
            if (param != null) {
                isCreateState = false;
                isEditMode = false;
                Long reportToViewId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reportToViewId"));
                Report report = this.reportSessionBeanLocal.retrieveReportById(reportToViewId);
                if (!currentEmployee.equals(report.getEmployee()) && report.getDatePublished() == null) {
                    this.report = null;
                    this.redirectMessage = "Report either does not belong to you or it is not published! Redirecting to Report Management.";
                } else if (!currentEmployee.equals(report.getEmployee()) && report.getDatePublished() != null) {
                    isViewState = true;
                    this.report = report;
                    processReport(this.report);
                } else {
                    this.report = report;
                    processReport(this.report);
                }
            } else {
                isViewState = false;
                isCreateState = true;
                isEditMode = true;

            }
        } catch (NumberFormatException ex) {
            this.report = null;
            this.redirectMessage = "Invalid Report ID! Redirecting to Report Management.";
        } catch (ReportNotFoundException ex) {
            this.report = null;
            this.redirectMessage = "Invalid Report ID! Redirecting to Report Management.";
        }
    }

    public void checkCreation() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        Object objStrMessage = flash.get("createReportSuccess");
        if (objStrMessage != null) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Create Report Success", (String) objStrMessage));
        }
    }

    private void refreshData() {
        this.servicemenData = servicemanSessionBeanLocal.retrieveAllServicemen();
        this.consultationsData = consultationSessionBeanLocal.retrieveAllConsultations();
        this.medicalOfficersData = employeeSessionBeanLocal.retrieveAllMedicalOfficers();
    }

    public void processReport(Report report) {
        reportFieldWrappers = report.getReportFields().stream()
                .map(field -> {
                    ReportFieldWrapper wrapper = new ReportFieldWrapper(field);
                    wrapper.createChart();
                    return wrapper;
                })
                .collect(Collectors.toList());
    }

    public void revProcessReport(Report report) {
        // Maintain order of the fields
        this.report.setReportFields(reportFieldWrappers.stream()
                .map(wrapper -> wrapper.getReportField())
                .collect(Collectors.toList())
        );
    }

    public void addHeader() {
        ReportField reportField = new ReportField();
        reportField.setType(ReportFieldType.HEADER);
        ReportFieldWrapper reportFieldWrapper = new ReportFieldWrapper(reportField);
        this.reportFieldWrappers.add(reportFieldWrapper);
        this.report.getReportFields().add(reportField);
    }

    public void doDeleteFieldWrapper(ReportFieldWrapper reportFieldWrapper) {
        if (this.report.getReportFields().contains(reportFieldWrapper.getReportField())) {
            this.report.getReportFields().remove(reportFieldWrapper.getReportField());
        }
        this.reportFieldWrappers.remove(reportFieldWrapper);
    }

    public void doShiftDown(Integer initPos) {
        Collections.swap(reportFieldWrappers, initPos, initPos + 1);
    }

    public void doShiftUp(Integer initPos) {
        Collections.swap(reportFieldWrappers, initPos, initPos - 1);
    }

    public Boolean checkAnyFieldRequiresDate() {
        return this.report.getReportFields().stream()
                .anyMatch(field -> {
                    ReportDataGrouping reportDataGrouping = field.getReportDataGrouping();
                    if (reportDataGrouping != null) {
                        return reportDataGrouping.requireDate();
                    }
                    return false;
                });
    }

    public void doOpenDialogFilterDate() {
        this.filterDateType = this.report.getFilterDateType();
        this.filterStartDate = this.report.getFilterStartDate();
        this.filterEndDate = this.report.getFilterEndDate();
        typeFilterDate();
    }

    public void doFilterDate() {
        this.report.setFilterDateType(this.filterDateType);
        if (filterDateType == FilterDateType.NONE) {
            this.report.setFilterStartDate(null);
        } else {
            this.report.setFilterStartDate(filterStartDate);
        }
        if (filterDateType == filterDateType.CUSTOM || filterDateType == filterDateType.WEEK) {
            this.report.setFilterEndDate(filterEndDate);
        } else {
            this.report.setFilterEndDate(null);
        }

        PrimeFaces.current().executeScript("PF('dialogFilterDate').hide()");

        if (checkAnyFieldRequiresDate()) {
            this.report.getReportFields().stream()
                    .filter(field -> {
                        ReportDataGrouping reportDataGrouping = field.getReportDataGrouping();
                        if (reportDataGrouping != null) {
                            return reportDataGrouping.requireDate();
                        }
                        return false;
                    })
                    .forEach(field -> {
                        field.setFilterDateType(this.report.getFilterDateType());
                        field.setFilterStartDate(this.report.getFilterStartDate());
                        field.setFilterEndDate(this.report.getFilterEndDate());
                        processField(field);
                    });
            reportFieldWrappers.forEach(wrapper -> {
                wrapper.createChart();
            });

        }
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

    public void doOpenDialogEditChart(ReportFieldWrapper reportFieldWrapper, Integer idx) {
        this.idxAddChart = 0;
        this.isAddMode = false;
        this.dialogHeaderAddChart = "Edit Chart";
        reportFieldToEditWrapperIdx = idx;
        reportFieldToAddWrapper = new ReportFieldWrapper(reportFieldWrapper);
    }

    public void doOpenDialogAddChart() {
        this.idxAddChart = 0;
        this.isAddMode = true;
        this.dialogHeaderAddChart = "Add Chart";
        ReportField reportFieldToAdd = new ReportField();
        reportFieldToAddWrapper = new ReportFieldWrapper(reportFieldToAdd);

        // ADD CHART DEFAULT VALUES
        reportFieldToAdd.setReportDataType(ReportDataType.SERVICEMAN);
        reportFieldToAdd.setReportDataValue(ReportDataValue.QTY);
        reportFieldToAdd.setReportDataGrouping(ReportDataGrouping.S_BT);

    }

    public void doDialogAddChartNextPage() {
        ReportField reportField = this.reportFieldToAddWrapper.getReportField();
        this.idxAddChart += 1;
        if (idxAddChart == 1) {
            if (checkAddChartDate()) {
                this.filterDateType = reportField.getFilterDateType();
                this.filterStartDate = reportField.getFilterStartDate();
                this.filterEndDate = reportField.getFilterEndDate();
                typeFilterDate();
            }
        } else if (idxAddChart == 2) {
            if (checkAddChartDate()) {
                if (this.report.getFilterDateType() == FilterDateType.NONE) {
                    reportField.setFilterDateType(this.filterDateType);
                    reportField.setFilterStartDate(this.filterStartDate);
                    if (filterDateType == filterDateType.CUSTOM || filterDateType == filterDateType.WEEK) {
                        reportField.setFilterEndDate(filterEndDate);
                    } else {
                        reportField.setFilterEndDate(null);
                    }
                } else {
                    reportField.setFilterDateType(this.report.getFilterDateType());
                    reportField.setFilterStartDate(this.report.getFilterStartDate());
                    reportField.setFilterEndDate(this.report.getFilterEndDate());
                }
            } else {
                reportField.setFilterDateType(FilterDateType.NONE);
                reportField.setFilterStartDate(null);
                reportField.setFilterEndDate(null);
            }
        } else if (idxAddChart == 3) {
            processField(reportField);
            reportFieldToAddWrapper.createChart();
        }
    }

    public Boolean checkAddChartDate() {
        ReportField reportField = this.reportFieldToAddWrapper.getReportField();
        return reportField.getReportDataGrouping().requireDate();
    }

    public String getAddChartbtnText() {
        Integer numOfData = getTotalNumberOfData(reportFieldToAddWrapper.getReportField());
        if (numOfData == 0) {
            return "No Data";
        } else {
            return isAddMode ? "Confirm Add Chart" : "Confirm Edit Chart";
        }
    }

    public void doConfirmAddChart() {
        if (isAddMode) {
            this.report.getReportFields().add(this.reportFieldToAddWrapper.getReportField());
            this.reportFieldWrappers.add(this.reportFieldToAddWrapper);
        } else {
            this.reportFieldWrappers.set(reportFieldToEditWrapperIdx, this.reportFieldToAddWrapper);
        }
    }

    private void processField(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();

        if (reportField.getReportDataType() == ReportDataType.SERVICEMAN) {
            sortedResultList = processDataServiceman(reportField);
        } else if (reportField.getReportDataType() == ReportDataType.MEDICAL_OFFICER) {
            sortedResultList = processDataMedicalOfficer(reportField);
        } else if (reportField.getReportDataType() == ReportDataType.CONSULTATION) {
            sortedResultList = processDataConsultation(reportField);
        }

        List<ReportFieldGroup> groups = reportField.getReportFieldGroups();
        groups.clear();

        sortedResultList.forEach((mapEntry) -> {
            groups.add(new ReportFieldGroup(mapEntry.getKey(), mapEntry.getValue()));
        });
    }

    private List<Map.Entry<String, Integer>> processDataServiceman(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        if (reportField.getReportDataGrouping() == ReportDataGrouping.S_BT) {
            HashMap<String, Integer> freqMap = new HashMap<>();
            this.servicemenData.stream()
                    .map(s -> s.getBloodType().getBloodTypeString())
                    .forEach(bt -> {
                        int count = freqMap.containsKey(bt) ? freqMap.get(bt) : 0;
                        freqMap.put(bt, count + 1);
                    });
            sortedResultList = sortByType(freqMap, "STRING");
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.S_BK) {
            HashMap<Integer, Integer> preFreqMap = new HashMap<>();
            this.servicemenData.stream()
                    .map(s -> (int) s.getBookings().stream()
                    .filter(b -> {
                        return b.getBookingStatusEnum() != BookingStatusEnum.ABSENT
                                && b.getBookingStatusEnum() != BookingStatusEnum.CANCELLED
                                && isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), b.getBookingSlot().getStartDateTime());
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
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.S_PES) {
            // TO IMPLEMENT
        }
        return sortedResultList;
    }

    private List<Map.Entry<String, Integer>> processDataMedicalOfficer(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        if (reportField.getReportDataGrouping() == ReportDataGrouping.MO_CS) {
            HashMap<Integer, Integer> preFreqMap = new HashMap<>();
            this.medicalOfficersData.stream()
                    .map(mo -> (int) mo.getCompletedConsultations().stream().filter(c -> isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), c.getStartDateTime())).count())
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
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.MO_FI) {
            HashMap<Integer, Integer> preFreqMap = new HashMap<>();
            this.medicalOfficersData.stream()
                    .map(mo -> (int) mo.getSignedFormInstances().stream().filter(fi -> isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), fi.getDateSubmitted())).count())
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
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.MO_MC) {
            HashMap<String, Integer> freqMap = new HashMap<>();
            this.medicalOfficersData.stream()
                    .filter(mo -> mo.getMedicalCentre() != null)
                    .map(mo -> mo.getMedicalCentre().getName())
                    .forEach(mc -> {
                        int count = freqMap.containsKey(mc) ? freqMap.get(mc) : 0;
                        freqMap.put(mc, count + 1);
                    });
            sortedResultList = sortByType(freqMap, "STRING");
        }
        return sortedResultList;
    }

    private List<Map.Entry<String, Integer>> processDataConsultation(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        if (reportField.getReportDataGrouping() == ReportDataGrouping.C_Q_MC) {
            HashMap<String, Integer> freqMap = new HashMap<>();
            this.consultationsData.stream()
                    .filter(c -> c.getConsultationStatusEnum() == ConsultationStatusEnum.COMPLETED && isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), c.getEndDateTime()))
                    .map(c -> c.getBooking().getBookingSlot().getMedicalCentre().getName())
                    .forEach(mc -> {
                        int count = freqMap.containsKey(mc) ? freqMap.get(mc) : 0;
                        freqMap.put(mc, count + 1);
                    });
            sortedResultList = sortByType(freqMap, "STRING");
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_Q_CP) {

        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_W_MC) {

        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_W_HR) {

        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_D_MC) {

        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_D_CP) {

        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_QT_MC) {
            Function<Consultation, MedicalCentre> extractMedicalCentre = c -> c.getBooking().getBookingSlot().getMedicalCentre();
            Map<MedicalCentre, List<Consultation>> consultationsByMc = this.consultationsData.stream()
                    .filter(c -> c.getConsultationStatusEnum() == ConsultationStatusEnum.COMPLETED && isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), c.getEndDateTime()))
                    .collect(Collectors.groupingBy(extractMedicalCentre, Collectors.toList()));
            reportField.getDatasetFields().clear();
            for (Map.Entry<MedicalCentre, List<Consultation>> entry : consultationsByMc.entrySet()) {
                List<Map.Entry<String, Integer>> datasetEntry = generateTrendDataset(reportField, entry.getValue());
                ReportField datasetField = new ReportField();
                datasetField.setName(entry.getKey().getName());
                datasetEntry.forEach((mapEntry) -> {
                    datasetField.getReportFieldGroups().add(new ReportFieldGroup(mapEntry.getKey(), mapEntry.getValue()));
                });
                reportField.getDatasetFields().add(datasetField);
            }
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_QT_CP) {

        }
        return sortedResultList;
    }

    private List<Map.Entry<String, Integer>> generateTrendDataset(ReportField reportField, List<Consultation> consultations) {
        // DATE CHECK HERE & PROCESS DATA

        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
//        HashMap<Integer, Integer> preFreqMap = new HashMap<>();
//        if (!preFreqMap.isEmpty()) {
//            int limit = (int) Math.ceil(preFreqMap.keySet().stream().max(Long::compare).get() / 5.0) * 5;
//            for (int i = 0; i < limit; i += 5) {
//                int min = i == 0 ? i : i + 1;
//                int max = i + 5;
//                Integer count = preFreqMap.entrySet().stream().filter(entry -> entry.getKey() >= min && entry.getKey() <= max).map(entry -> entry.getValue()).reduce(0, (x, y) -> x + y);
//                String str = String.valueOf(min) + " - " + String.valueOf(max);
//                Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<String, Integer>(str, count);
//                sortedResultList.add(entry);
//            }
//        }
        return sortedResultList;
    }

    private List<Map.Entry<String, Integer>> sortByType(HashMap<String, Integer> map, String type) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        if (type.equals("STRING")) {
            list = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(String::toString)))
                    .collect(Collectors.toList());
        }
        return list;
    }

    public void doDelete() {
        try {
            reportSessionBeanLocal.deleteReport(this.report.getReportId());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("deleteReport", "Successfully deleted " + this.report.toString());
            FacesContext.getCurrentInstance().getExternalContext().redirect("report-management.xhtml");
        } catch (DeleteReportException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete Report", ex.getMessage()));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
        }
    }

    public void doCreate() {
        try {
            this.report.setLastModified(new Date());
            revProcessReport(report);
            this.report = reportSessionBeanLocal.createReport(this.report, currentEmployee.getEmployeeId());

            String result = "Successfully created " + this.report;
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("createReportSuccess", result);
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String url = request.getRequestURL().toString() + "?reportToViewId=" + this.report.getReportId();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (CreateReportException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create Report", ex.getMessage()));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
        }
    }

    public void doEdit() {
        if (isEditMode) {
            try {
                report.setLastModified(new Date());
                revProcessReport(report);
                this.report = reportSessionBeanLocal.updateReport(this.report);
                processReport(this.report);
                this.isCreateState = false;
                isEditMode = false;
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Update Report", "Successfully updated " + this.report));
            } catch (UpdateReportException ex) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update Report", ex.getMessage()));
            }
        } else {
            isEditMode = true;
        }
    }

    public void doClone() {
        try {
            Report report = reportSessionBeanLocal.cloneReport(this.report.getReportId(), currentEmployee.getEmployeeId());

            String result = "Successfully cloned " + report;
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("createReportSuccess", result);
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String url = request.getRequestURL().toString() + "?reportToViewId=" + report.getReportId();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (CloneReportException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Clone Report", ex.getMessage()));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
        }
    }

    public void doPublish() {
        try {
            this.report = reportSessionBeanLocal.publishReport(report.getReportId());
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Publish Report", "Successfully published " + this.report));
        } catch (PublishReportException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Publish Report", ex.getMessage()));
        }
    }

    public void doUnpublish() {
        try {
            this.report = reportSessionBeanLocal.unpublishReport(report.getReportId());
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Unpublish Report", "Successfully unpublished " + this.report));
        } catch (PublishReportException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unpublish Report", ex.getMessage()));
        }
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
        ReportDataType reportDataType = this.reportFieldToAddWrapper.getReportField().getReportDataType();
        if (reportDataType == null) {
            return ReportDataValue.values();
        } else {
            return ReportDataValue.getReportDataTypeValues(reportDataType);
        }
    }

    public ReportDataGrouping[] getReportDataGroupings() {
        ReportDataType reportDataType = this.reportFieldToAddWrapper.getReportField().getReportDataType();
        ReportDataValue reportDataValue = this.reportFieldToAddWrapper.getReportField().getReportDataValue();
        if (reportDataType == null) {
            return null;
        } else {
            return ReportDataGrouping.getReportDataGroupings(reportDataType, reportDataValue);
        }
    }

    public ReportFieldType[] getReportFieldTypes() {
        ReportDataGrouping reportDataGrouping = this.reportFieldToAddWrapper.getReportField().getReportDataGrouping();
        if (reportDataGrouping == null) {
            return ReportFieldType.values();
        } else {
            return ReportFieldType.getReportFieldTypes(reportDataGrouping);
        }
    }

    public String getDialogHeaderAddChart() {
        return dialogHeaderAddChart;
    }

    public Integer getTotalNumberOfData(ReportField reportField) {
        return reportField.getReportFieldGroups().stream()
                .map(grp -> grp.getQuantity())
                .reduce(0, (x, y) -> x + y);
    }

    public ReportFieldWrapper getReportFieldToAddWrapper() {
        return reportFieldToAddWrapper;
    }

    public void setReportFieldToAddWrapper(ReportFieldWrapper reportFieldToAddWrapper) {
        this.reportFieldToAddWrapper = reportFieldToAddWrapper;
    }

    public List<ReportFieldWrapper> getReportFieldWrappers() {
        return reportFieldWrappers;
    }

    public void setReportFieldWrappers(List<ReportFieldWrapper> reportFieldWrappers) {
        this.reportFieldWrappers = reportFieldWrappers;
    }

    public Boolean getIsAddMode() {
        return isAddMode;
    }

    public void setIsAddMode(Boolean isAddMode) {
        this.isAddMode = isAddMode;
    }

    public Boolean getIsCreateState() {
        return isCreateState;
    }

    public void setIsCreateState(Boolean isCreateState) {
        this.isCreateState = isCreateState;
    }

    public Boolean getIsViewState() {
        return isViewState;
    }

    public void setIsViewState(Boolean isViewState) {
        this.isViewState = isViewState;
    }

}
