package ejb.session.stateless;

import entity.ConditionStatus;
import entity.Employee;
import entity.Serviceman;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.Local;
import util.enumeration.PesStatusEnum;

@Local
public interface EmailSessionBeanLocal {

    public Future<Boolean> emailEmployeeOtpAsync(Employee employee, String otp) throws InterruptedException;

    public Future<Boolean> emailServicemanOtpAsync(Serviceman serviceman, String otp) throws InterruptedException;

    public Future<Boolean> emailServicemanResetPasswordAsync(Serviceman serviceman, String otp) throws InterruptedException;

    public Future<Boolean> emailEmployeeResetPasswordAsync(Employee employee, String otp) throws InterruptedException;

    public Future<Boolean> emailServicemanChangeEmailAsync(Serviceman serviceman) throws InterruptedException;

    public Future<Boolean> emailEmployeeChangeEmailAsync(Employee employee) throws InterruptedException;

    public Future<Boolean> emailServicemanBoardResult(Serviceman serviceman, PesStatusEnum pesStatusEnum, List<ConditionStatus> conditionStatuses) throws InterruptedException;

}
