/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Report;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.ReportNotFoundException;
import util.exceptions.CloneReportException;
import util.exceptions.CreateReportException;
import util.exceptions.DeleteReportException;
import util.exceptions.PublishReportException;
import util.exceptions.UpdateReportException;

/**
 *
 * @author Bryan
 */
@Local
public interface ReportSessionBeanLocal {

    public Report createReport(Report report, Long employeeId) throws CreateReportException;

    public Report retrieveReportById(Long id) throws ReportNotFoundException;

    public List<Report> retrieveAllReports();

    public void deleteReport(Long reportId) throws DeleteReportException;

    public Report updateReport(Report report) throws UpdateReportException;

    public Report publishReport(Long reportId) throws PublishReportException;

    public Report unpublishReport(Long reportId) throws PublishReportException;

    public Report cloneReport(Long reportId, Long employeeId) throws CloneReportException;
    
}
