package ejb.session.stateless;

import entity.Employee;
import entity.Report;
import entity.ReportField;
import entity.ReportFieldGroup;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.ReportNotFoundException;
import util.exceptions.CreateReportException;
import util.exceptions.DeleteReportException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.UpdateReportException;

@Stateless
public class ReportSessionBean implements ReportSessionBeanLocal {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public ReportSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Report retrieveReportById(Long id) throws ReportNotFoundException {
        Report report = em.find(Report.class, id);

        if (report != null) {
            return report;
        } else {
            throw new ReportNotFoundException("Report ID " + id + " does not exist!");
        }
    }

    @Override
    public List<Report> retrieveAllReports() {
        Query query = em.createQuery("SELECT r FROM Report r");

        return query.getResultList();
    }

    @Override
    public Report createReport(Report report, Long employeeId) throws CreateReportException {
        String errorMessage = "Failed to create Report: ";
        try {
            Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);

            Set<ConstraintViolation<Report>> constraintViolations = validator.validate(report);
            Set<ConstraintViolation<ReportField>> constraintViolationsFields = new HashSet<>();
            Set<ConstraintViolation<ReportFieldGroup>> constraintViolationsGroups = new HashSet<>();
            report.getReportFields().forEach(field -> {
                constraintViolationsFields.addAll(validator.validate(field));
                field.getReportFieldGroups().forEach(grp -> {
                    constraintViolationsGroups.addAll(validator.validate(grp));
                });
            });

            if (!constraintViolations.isEmpty()) {
                throw new CreateReportException(prepareReportInputDataValidationErrorsMessage(constraintViolations));
            } else if (!constraintViolationsFields.isEmpty()) {
                throw new CreateReportException(prepareReportFieldInputDataValidationErrorsMessage(constraintViolationsFields));
            } else if (!constraintViolationsGroups.isEmpty()) {
                throw new CreateReportException(prepareReportFieldGroupInputDataValidationErrorsMessage(constraintViolationsGroups));
            }

            report.getReportFields().forEach(field -> {
                field.getReportFieldGroups().forEach(grp -> {
                    em.persist(grp);
                    em.flush();
                });
                em.persist(field);
                em.flush();
            });

            em.persist(report);
            report.setEmployee(employee);
            employee.getReports().add(report);

            return report;
        } catch (CreateReportException ex) {
            throw new CreateReportException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new CreateReportException(errorMessage + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreateReportException(generalUnexpectedErrorMessage + "creating report");
        }

    }

    @Override
    public Report updateReport(Report report) throws UpdateReportException {
        String errorMessage = "Failed to update Report: ";
        try {
            Report reportToUpdate = retrieveReportById(report.getReportId());

            Set<ConstraintViolation<Report>> constraintViolations = validator.validate(report);
            Set<ConstraintViolation<ReportField>> constraintViolationsFields = new HashSet<>();
            Set<ConstraintViolation<ReportFieldGroup>> constraintViolationsGroups = new HashSet<>();
            report.getReportFields().forEach(field -> {
                constraintViolationsFields.addAll(validator.validate(field));
                field.getReportFieldGroups().forEach(grp -> {
                    constraintViolationsGroups.addAll(validator.validate(grp));
                });
            });

            if (!constraintViolations.isEmpty()) {
                throw new UpdateReportException(prepareReportInputDataValidationErrorsMessage(constraintViolations));
            } else if (!constraintViolationsFields.isEmpty()) {
                throw new UpdateReportException(prepareReportFieldInputDataValidationErrorsMessage(constraintViolationsFields));
            } else if (!constraintViolationsGroups.isEmpty()) {
                throw new UpdateReportException(prepareReportFieldGroupInputDataValidationErrorsMessage(constraintViolationsGroups));
            }

            report.getReportFields().forEach(field -> {
                field.getReportFieldGroups().forEach(grp -> {
                    em.persist(grp);
                    em.flush();
                });
                em.persist(field);
                em.flush();
            });
            
            reportToUpdate.setReportFields(report.getReportFields());
            reportToUpdate.setName(report.getName());
            reportToUpdate.setDescription(report.getDescription());
            reportToUpdate.setDateCreated(report.getDateCreated());
            reportToUpdate.setDatePublished(report.getDatePublished());
            reportToUpdate.setLastModified(report.getLastModified());
            reportToUpdate.setReportStatus(report.getReportStatus());
            reportToUpdate.setFilterDateType(report.getFilterDateType());
            reportToUpdate.setFilterStartDate(report.getFilterStartDate());
            reportToUpdate.setFilterEndDate(report.getFilterEndDate());
            
            em.flush();

            return report;
        } catch (UpdateReportException | ReportNotFoundException ex) {
            throw new UpdateReportException(errorMessage + ex.getMessage());
        } 
//        catch (Exception ex) {
//            ex.printStackTrace();
//            throw new UpdateReportException(generalUnexpectedErrorMessage + "creating updating report");
//        }

    }

    @Override
    public void deleteReport(Long reportId) throws DeleteReportException {
        String errorMessage = "Failed to delete Report: ";
        try {
            Report report = retrieveReportById(reportId);
            report.getEmployee().getReports().remove(report);
            em.remove(report);
        } catch (ReportNotFoundException ex) {
            throw new DeleteReportException(errorMessage + ex.getMessage());
        }
    }

    private String prepareReportInputDataValidationErrorsMessage(Set<ConstraintViolation<Report>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

    private String prepareReportFieldInputDataValidationErrorsMessage(Set<ConstraintViolation<ReportField>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

    private String prepareReportFieldGroupInputDataValidationErrorsMessage(Set<ConstraintViolation<ReportFieldGroup>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

}
