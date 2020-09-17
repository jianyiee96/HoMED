package ejb.session.stateless;

import entity.Employee;
import entity.Serviceman;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
    public Future<Boolean> emailEmployeeOtpAsync(Employee employee, String otp) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailEmployeeOtp(employee, otp, FROM_EMAIL);

        return new AsyncResult<>(result);
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailServicemanOtpAsync(Serviceman serviceman, String otp) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailServicemanOtp(serviceman, otp, FROM_EMAIL);

        return new AsyncResult<>(result);
    }
}
