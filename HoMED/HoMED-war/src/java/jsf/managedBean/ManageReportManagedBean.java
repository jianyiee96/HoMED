package jsf.managedBean;

import com.oracle.jrockit.jfr.DataType;
import ejb.session.stateless.ConsultationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ReportSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Consultation;
import entity.ConsultationPurpose;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalOfficer;
import entity.Report;
import entity.ReportField;
import entity.ReportFieldGroup;
import entity.Serviceman;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

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
            if (end.after(new Date())) {
                end = getFloorDay(new Date());
            }
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

    }

    private void processSortedListIntoField(ReportField reportField, List<Map.Entry<String, Integer>> sortedResultList) {
        List<ReportFieldGroup> groups = reportField.getReportFieldGroups();
        groups.clear();

        sortedResultList.forEach((mapEntry) -> {
            groups.add(new ReportFieldGroup(mapEntry.getKey(), mapEntry.getValue()));
        });
    }

    private List<Map.Entry<String, Integer>> processDataServiceman(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        if (reportField.getReportDataGrouping() == ReportDataGrouping.S_BT) {
            sortedResultList = transformIntoCategorizationList(servicemenData, (Serviceman s) -> s.getBloodType().getBloodTypeString(), (Serviceman s) -> true);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Blood Type");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.S_BK) {
            Function<Serviceman, Integer> serivcemanBookingCounter = s -> (int) s.getBookings().stream()
                    .filter(b -> {
                        return b.getBookingStatusEnum() != BookingStatusEnum.ABSENT
                                && b.getBookingStatusEnum() != BookingStatusEnum.CANCELLED
                                && isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), b.getBookingSlot().getStartDateTime());
                    }).count();
            sortedResultList = transformIntoCounterList(servicemenData, serivcemanBookingCounter, 5);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Number of Bookings");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.S_PES) {
            // TO IMPLEMENT
            reportField.setX_axis("PES Status");
        }
        reportField.setY_axis("Number of Serviceman");
        return sortedResultList;
    }

    private List<Map.Entry<String, Integer>> processDataMedicalOfficer(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        if (reportField.getReportDataGrouping() == ReportDataGrouping.MO_CS) {
            Function<MedicalOfficer, Integer> medicalOfficerConsultationsCounter = mo -> (int) mo.getCompletedConsultations().stream()
                    .filter(c -> isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), c.getStartDateTime()))
                    .count();
            sortedResultList = transformIntoCounterList(medicalOfficersData, medicalOfficerConsultationsCounter, 5);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Number of Consultations");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.MO_FI) {
            Function<MedicalOfficer, Integer> medicalOfficerConsultationsFormsSignedCounter = mo -> (int) mo.getSignedFormInstances().stream()
                    .filter(fi -> isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), fi.getDateSubmitted()))
                    .count();
            sortedResultList = transformIntoCounterList(medicalOfficersData, medicalOfficerConsultationsFormsSignedCounter, 5);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Number of Forms Signed");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.MO_MC) {
            sortedResultList = transformIntoCategorizationList(medicalOfficersData, (MedicalOfficer mo) -> mo.getMedicalCentre().getName(), (MedicalOfficer mo) -> mo.getMedicalCentre() != null);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Medical Centre");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        }

        reportField.setY_axis("Number of Medical Officers");
        return sortedResultList;
    }

    private List<Map.Entry<String, Integer>> processDataConsultation(ReportField reportField) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();
        List<Consultation> dateFilteredConsultations = this.consultationsData.stream()
                .filter(c -> c.getConsultationStatusEnum() == ConsultationStatusEnum.COMPLETED && isWithinRange(reportField.getFilterDateType(), reportField.getFilterStartDate(), reportField.getFilterEndDate(), c.getEndDateTime()))
                .collect(Collectors.toList());

        if (reportField.getReportDataGrouping() == ReportDataGrouping.C_Q_MC) {
            sortedResultList = transformIntoCategorizationList(dateFilteredConsultations, (Consultation c) -> c.getBooking().getBookingSlot().getMedicalCentre().getName(), (Consultation c) -> true);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Medical Centre");
            reportField.setY_axis("Number of Consultations");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_Q_CP) {
            sortedResultList = transformIntoCategorizationList(dateFilteredConsultations, (Consultation c) -> c.getBooking().getConsultationPurpose().getConsultationPurposeName(), (Consultation c) -> true);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Consultation Purpose");
            reportField.setY_axis("Number of Consultations");
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_W_MC) {
            ToIntFunction<Consultation> queueWaitingTime = c -> {
                return (int) ((c.getStartDateTime().getTime() - c.getJoinQueueDateTime().getTime()) / (1000 * 60)) % 60;
            };
            sortedResultList = transformIntoCategorizationAverageList(dateFilteredConsultations, (Consultation c) -> c.getBooking().getBookingSlot().getMedicalCentre().getName(), queueWaitingTime);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Medical Centre");
            reportField.setY_axis("Queue Waiting Time (mins)");
            reportField.setAggregateString("Average Queue Waiting Time(mins): " + getAverageData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_W_HR) {
            ToIntFunction<Consultation> queueWaitingTime = c -> {
                return (int) ((c.getStartDateTime().getTime() - c.getJoinQueueDateTime().getTime()) / (1000 * 60)) % 60;
            };

            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                String key = String.format("%02d", i);
                Integer hourChecker = i;
                Integer count = (int) dateFilteredConsultations.stream()
                        .filter(c -> c.getJoinQueueDateTime().getHours() == hourChecker)
                        .mapToInt(queueWaitingTime)
                        .average()
                        .orElse(0);
                sortedList.add(new AbstractMap.SimpleEntry<String, Integer>(key, count));
            }
            ReportField rf = new ReportField();
            rf.setName("Dataset");
            processSortedListIntoField(rf, sortedList);
            reportField.getDatasetFields().clear();
            reportField.getDatasetFields().add(rf);
            reportField.setX_axis("Hour Of Day");
            reportField.setY_axis("Queue Waiting Time (mins)");
            reportField.setAggregateString("Average Queue Waiting Time(mins): " + getAverageData(rf));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_D_MC) {
            ToIntFunction<Consultation> consultationDuration = c -> {
                return (int) ((c.getEndDateTime().getTime() - c.getStartDateTime().getTime()) / (1000 * 60)) % 60;
            };
            sortedResultList = transformIntoCategorizationAverageList(dateFilteredConsultations, (Consultation c) -> c.getBooking().getBookingSlot().getMedicalCentre().getName(), consultationDuration);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Medical Centre");
            reportField.setY_axis("Consultation Duration(mins)");
            reportField.setAggregateString("Average Consultation Duration(mins): " + getAverageData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_D_CP) {
            ToIntFunction<Consultation> consultationDuration = c -> {
                return (int) ((c.getEndDateTime().getTime() - c.getStartDateTime().getTime()) / (1000 * 60)) % 60;
            };
            sortedResultList = transformIntoCategorizationAverageList(dateFilteredConsultations, (Consultation c) -> c.getBooking().getConsultationPurpose().getConsultationPurposeName(), consultationDuration);
            processSortedListIntoField(reportField, sortedResultList);
            reportField.setX_axis("Consultation Purpose");
            reportField.setY_axis("Consultation Duration(mins)");
            reportField.setAggregateString("Average Consultation Duration(mins): " + getAverageData(reportField));
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_QT_MC) {
            transformTrendField(reportField, dateFilteredConsultations, (Consultation c) -> c.getEndDateTime(), (Consultation c) -> c.getBooking().getBookingSlot().getMedicalCentre().getName());
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
            reportField.setY_axis("Number of Consultations");
        } else if (reportField.getReportDataGrouping() == ReportDataGrouping.C_QT_CP) {
            transformTrendField(reportField, dateFilteredConsultations, (Consultation c) -> c.getEndDateTime(), (Consultation c) -> c.getBooking().getConsultationPurpose().getConsultationPurposeName());
            reportField.setAggregateString("Total Quantity: " + getTotalNumberOfData(reportField));
            reportField.setY_axis("Number of Consultations");
        }

        return sortedResultList;
    }

    // USED FOR PRE-PROCESSING INTO COUNTER (Number of forms signed)
    private <T> List<Map.Entry<String, Integer>> transformIntoCounterList(List<T> list, Function<T, Integer> mapperToInt, Integer step) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        list.stream()
                .map(mapperToInt)
                .forEach(counter -> {
                    int count = hashMap.containsKey(counter) ? hashMap.get(counter) : 0;
                    hashMap.put(counter, count + 1);
                });
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>();
        if (!hashMap.isEmpty()) {
            int limit = (int) Math.ceil(hashMap.keySet().stream().max(Integer::compare).get() / (double) step) * step;
            for (int i = 0; i < limit; i += step) {
                int min = i == 0 ? i : i + 1;
                int max = i + step;
                Integer count = hashMap.entrySet().stream().filter(entry -> entry.getKey() >= min && entry.getKey() <= max).map(entry -> entry.getValue()).reduce(0, (x, y) -> x + y);
                String str = String.valueOf(min) + " - " + String.valueOf(max);
                Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<String, Integer>(str, count);
                sortedList.add(entry);
            }
        }
        return sortedList;
    }

    // USED FOR CATEGORIZING INTO SPECIFIC ENTITIES (Average Waiting Time, Averaege Consultation Time)
    private <T> List<Map.Entry<String, Integer>> transformIntoCategorizationAverageList(List<T> list, Function<T, String> mapperToGrouping, ToIntFunction<T> mapperToInt) {
        Map<String, List<T>> groupings = list.stream()
                .collect(Collectors.groupingBy(mapperToGrouping, Collectors.toList()));

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>();

        for (Map.Entry<String, List<T>> entry : groupings.entrySet()) {
            IntStream streamOfQueueTime = entry.getValue().stream().mapToInt(mapperToInt);
            sortedList.add(new AbstractMap.SimpleEntry<String, Integer>(entry.getKey(), (int) streamOfQueueTime.average().orElse(0)));
        }
        return sortedList;
    }

    // USED FOR CATEGORIZING INTO SPECIFIC ENTITIES (Consultation Purpose, Medical Centre)
    private <T> List<Map.Entry<String, Integer>> transformIntoCategorizationList(List<T> list, Function<T, String> keyItemFunction, Predicate<T> check) {
        HashMap<String, Integer> freqMap = new HashMap<>();
        list.stream()
                .filter(check)
                .map(keyItemFunction)
                .forEach(item -> {
                    int count = freqMap.containsKey(item) ? freqMap.get(item) : 0;
                    freqMap.put(item, count + 1);
                });
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>();
        sortedList = freqMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(String::toString)))
                .collect(Collectors.toList());
        return sortedList;
    }

    // USED FOR TREND LINE CHARTS
    private <T> void transformTrendField(ReportField reportField, List<T> list, Function<T, Date> mapperToDate, Function<T, String> mapperToGrouping) {
        reportField.getDatasetFields().clear();

        if (!list.isEmpty()) {
            List<Date> dates = list.stream()
                    .map(mapperToDate)
                    .collect(Collectors.toList());

            Integer dateType = identifyDateType(reportField, dates);
            setDateAxisLabelForField(reportField, dateType);
            List<String> baseDateAxes = generateBaseDateAxes(dateType, reportField, dates);

            // Processing by Consultatiton Purpose
            Map<String, List<T>> groupings = list.stream()
                    .collect(Collectors.groupingBy(mapperToGrouping, Collectors.toList()));
            for (Map.Entry<String, List<T>> entry : groupings.entrySet()) {
                List<Date> dateValues = entry.getValue().stream()
                        .map(mapperToDate)
                        .collect(Collectors.toList());

                List<Map.Entry<String, Integer>> datasetEntry = generateTrendDataset(dateValues, baseDateAxes, dateType);
                ReportField datasetField = new ReportField();
                datasetField.setType(ReportFieldType.DATASET);
                datasetField.setName(entry.getKey());
                datasetEntry.forEach((mapEntry) -> {
                    datasetField.getReportFieldGroups().add(new ReportFieldGroup(mapEntry.getKey(), mapEntry.getValue()));
                });
                reportField.getDatasetFields().add(datasetField);
            }
        }
    }

    private List<Map.Entry<String, Integer>> generateTrendDataset(List<Date> dates, List<String> baseDateAxes, Integer dateType) {
        List<Map.Entry<String, Integer>> sortedResultList = new ArrayList<>();

        Function<Date, String> functionDateString = x -> {
            if (dateType == 0) {
                return String.format("%02d", x.getHours());
            } else if (dateType == 1) {
                return dateFormat.format(x);
            } else if (dateType == 2) {
                return String.valueOf(x.getDate());
            } else if (dateType == 3) {
                return monthFormat.format(x);
            }
            return "";
        };
        for (String baseAxes : baseDateAxes) {
            Integer count = (int) dates.stream().map(functionDateString).filter(x -> x.equals(baseAxes)).count();
            Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<String, Integer>(baseAxes, count);
            sortedResultList.add(entry);
        }
        return sortedResultList;
    }

    // Return an integer type to be able to know which factor of each date field to be used (HR[00-23], DATE[dd/MM], DayInMonth[1-31], MONTH[MMM])
    private Integer identifyDateType(ReportField reportField, List<Date> dates) {
        // 0 - Hour
        // 1 - Date
        // 2 - DayInMonth
        // 3 - Month
        if (reportField.getFilterDateType() == FilterDateType.NONE || reportField.getFilterDateType() == FilterDateType.CUSTOM) {
            Date minDate;
            Date maxDate;
            if (reportField.getFilterDateType() == FilterDateType.NONE) {
                // NONE
                minDate = dates.stream().min((x, y) -> x.compareTo(y)).get();
                maxDate = dates.stream().max((x, y) -> x.compareTo(y)).get();
            } else {
                // CUSTOM
                minDate = reportField.getFilterStartDate();
                maxDate = reportField.getFilterEndDate();
            }
            long diff = maxDate.getTime() - minDate.getTime();
            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            if (diff > 60) {
                return 3;
            } else {
                return 1;
            }
        } else if (reportField.getFilterDateType() == FilterDateType.DAY) {
            return 0;
        } else if (reportField.getFilterDateType() == FilterDateType.WEEK) {
            return 1;
        } else if (reportField.getFilterDateType() == FilterDateType.MONTH) {
            return 2;
        } else if (reportField.getFilterDateType() == FilterDateType.YEAR) {
            return 3;
        }
        return -1;
    }

    private void setDateAxisLabelForField(ReportField reportField, Integer dateType) {
        // 0 - Hour
        // 1 - Date
        // 2 - DayInMonth
        // 3 - Month
        if (dateType == 0) {
            reportField.setX_axis("Hour");
        } else if (dateType == 1) {
            reportField.setX_axis("Date");
        } else if (dateType == 2) {
            reportField.setX_axis("Day In Month");
        } else if (dateType == 3) {
            reportField.setX_axis("Month");
        }

    }

    private List<String> generateBaseDateAxes(Integer dateType, ReportField reportField, List<Date> dates) {
        List<String> dateAxes = new ArrayList<>();
        Date minDate = new Date();
        Date maxDate = new Date();
        if (reportField.getFilterDateType() == FilterDateType.NONE) {
            minDate = dates.stream().min((x, y) -> x.compareTo(y)).get();
            maxDate = dates.stream().max((x, y) -> x.compareTo(y)).get();
        } else {
//             if (reportField.getFilterDateType() == FilterDateType.CUSTOM || reportField.getFilterDateType() == FilterDateType.WEEK)
            minDate = reportField.getFilterStartDate();
            maxDate = reportField.getFilterEndDate();
        }

        if (dateType == 0) {
            for (int i = 0; i < 24; i++) {
                dateAxes.add(String.format("%02d", i));
            }
        } else if (dateType == 1) {
            Calendar indexCal = new GregorianCalendar();
            indexCal.setTime(minDate);
            while (!indexCal.getTime().after(maxDate)) {
                dateAxes.add(dateFormat.format(indexCal.getTime()));
                indexCal.add(Calendar.DATE, 1);
            }
        } else if (dateType == 2) {
            Calendar indexCal = new GregorianCalendar();
            indexCal.setTime(minDate);
            Integer numberOfDays = indexCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= numberOfDays; i++) {
                dateAxes.add(String.valueOf(i));
            }
        } else if (dateType == 3) {
            Calendar indexCal = new GregorianCalendar();
            if (reportField.getFilterDateType() == FilterDateType.YEAR) {
                indexCal.setTime(minDate);
                for (int i = 1; i <= 12; i++) {
                    dateAxes.add(monthFormat.format(indexCal.getTime()));
                    indexCal.add(Calendar.MONTH, 1);
                }
            } else {
                Integer m1 = minDate.getYear() * 12 + minDate.getMonth();
                Integer m2 = maxDate.getYear() * 12 + maxDate.getMonth();
                Integer monthDifference = m2 - m1 + 1;
                indexCal.setTime(minDate);
                for (int i = 0; i < monthDifference; i++) {
                    dateAxes.add(monthFormat.format(indexCal.getTime()));
                    indexCal.add(Calendar.MONTH, 1);
                }
            }
        }

        return dateAxes;
    }

    public Integer getAverageData(ReportField reportField) {
        return (int) reportField.getReportFieldGroups().stream()
                .mapToInt(grp -> grp.getQuantity())
                .filter(x -> x != 0)
                .average()
                .orElse(0);
    }

    public Integer getTotalNumberOfData(ReportField reportField) {
        if (reportField.getType() == ReportFieldType.LINE) {
            return reportField.getDatasetFields().stream()
                    .flatMap(field -> field.getReportFieldGroups().stream())
                    .map(grp -> grp.getQuantity())
                    .reduce(0, (x, y) -> x + y);
        }
        return reportField.getReportFieldGroups().stream()
                .map(grp -> grp.getQuantity())
                .reduce(0, (x, y) -> x + y);
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
