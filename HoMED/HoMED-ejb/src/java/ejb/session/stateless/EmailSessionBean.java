package ejb.session.stateless;

import entity.Employee;
import entity.Serviceman;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import util.email.EmailManager;

@Stateless
public class EmailSessionBean implements EmailSessionBeanLocal {

    private String GMAIL_USERNAME = "HoMED.mailer@gmail.com";
    private String GMAIL_PASSWORD = "homedsystemadmin";
    private String FROM_EMAIL = "HoMED Admin <HoMED.mailer@gmail.com>";

    public EmailSessionBean() {
    }
    
    @Asynchronous
    @Override
    public Future<Boolean> emailEmployeeChangeEmailAsync(Employee employee) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailEmployeeChangeEmail(employee, FROM_EMAIL);

        return new AsyncResult<>(result);
    }
    
    @Asynchronous
    @Override
    public Future<Boolean> emailEmployeeOtpAsync(Employee employee, String otp) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailEmployeeOtp(employee, otp, FROM_EMAIL);

        return new AsyncResult<>(result);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailEmployeeResetPasswordAsync(Employee employee, String otp) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailEmployeeResetPassword(employee, otp, FROM_EMAIL);

        return new AsyncResult<>(result);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailServicemanChangeEmailAsync(Serviceman serviceman) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailServicemanChangeEmail(serviceman, FROM_EMAIL);

        return new AsyncResult<>(result);
    }
    
    @Asynchronous
    @Override
    public Future<Boolean> emailServicemanOtpAsync(Serviceman serviceman, String otp) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailServicemanOtp(serviceman, otp, FROM_EMAIL);

        return new AsyncResult<>(result);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailServicemanResetPasswordAsync(Serviceman serviceman, String otp) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailServicemanResetPassword(serviceman, otp, FROM_EMAIL);

        return new AsyncResult<>(result);
    }
}
