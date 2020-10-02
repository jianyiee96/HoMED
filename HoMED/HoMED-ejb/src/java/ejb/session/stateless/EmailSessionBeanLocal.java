package ejb.session.stateless;

import entity.Employee;
import entity.Serviceman;
import java.util.concurrent.Future;
import javax.ejb.Local;

@Local
public interface EmailSessionBeanLocal {

    public Future<Boolean> emailEmployeeOtpAsync(Employee employee, String otp) throws InterruptedException;

    public Future<Boolean> emailServicemanOtpAsync(Serviceman serviceman, String otp) throws InterruptedException;

    public Future<Boolean> emailServicemanResetPasswordAsync(Serviceman serviceman, String otp) throws InterruptedException;

    public Future<Boolean> emailEmployeeResetPasswordAsync(Employee employee, String otp) throws InterruptedException;

    public Future<Boolean> emailServicemanChangeEmailAsync(Serviceman serviceman) throws InterruptedException;

    public Future<Boolean> emailEmployeeChangeEmailAsync(Employee employee) throws InterruptedException;

}
