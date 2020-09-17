package util.email;

import entity.Employee;
import entity.Serviceman;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailManager {

    private final String emailServerName = "smtp.gmail.com";
    private final String mailer = "JavaMailer";
    private String smtpAuthUser;
    private String smtpAuthPassword;

    private String linkToHomed = "http://localhost:8080/HoMED-war/login.xhtml";

    public EmailManager() {
    }

    public EmailManager(String smtpAuthUser, String smtpAuthPassword) {
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
    }

    public Boolean emailEmployeeOtp(Employee employee, String otp, String fromEmailAddress) {
        String toEmail = employee.getEmail();
        String emailSubject = "HoMED Employee Account Activation Required";
        String emailBody = createEmailBodyEmployeeOtp(employee, otp);
        try {
            Session session = createSession();
            // REMOVE COMMENTS HERE
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                msg.setFrom(InternetAddress.parse(fromEmailAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setSubject(emailSubject);
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(emailBody, "UTF-8", "html");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
                msg.setHeader("X-Mailer", mailer);
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public Boolean emailServicemanOtp(Serviceman serviceman, String otp, String fromEmailAddress) {
        String toEmail = serviceman.getEmail();
        String emailSubject = "HoMED Serviceman Account Activation Required";
        String emailBody = createEmailBodyServicemanOtp(serviceman, otp);
        try {
            Session session = createSession();
            // REMOVE COMMENTS HERE
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                msg.setFrom(InternetAddress.parse(fromEmailAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setSubject(emailSubject);
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(emailBody, "UTF-8", "html");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
                msg.setHeader("X-Mailer", mailer);
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", emailServerName);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
        Session session = Session.getInstance(props, auth);
        return session;
    }

    private String createEmailBodyServicemanOtp(Serviceman serviceman, String otp) {
        return "<div>\n"
                + "   <style>\n"
                + "      <!--\n"
                + "         .rps_daf5 body\n"
                + "         	{margin:0}\n"
                + "         .rps_daf5 h1 a:hover\n"
                + "         	{font-size:30px;\n"
                + "         	color:#333}\n"
                + "         .rps_daf5 h1 a:active\n"
                + "         	{font-size:30px;\n"
                + "         	color:#333}\n"
                + "         .rps_daf5 h1 a:visited\n"
                + "         	{font-size:30px;\n"
                + "         	color:#333}\n"
                + "         .rps_daf5 a:hover\n"
                + "         	{text-decoration:none}\n"
                + "         .rps_daf5 a:active\n"
                + "         	{text-decoration:none}\n"
                + "         .rps_daf5 a:visited\n"
                + "         	{text-decoration:none}\n"
                + "         .rps_daf5 a:hover\n"
                + "         	{color:#1990C6}\n"
                + "         .rps_daf5 a:active\n"
                + "         	{color:#1990C6}\n"
                + "         .rps_daf5 a:visited\n"
                + "         	{color:#1990C6}\n"
                + "         @media (max-width: 600px) {\n"
                + "         .rps_daf5 .x_container\n"
                + "         	{width:94%!important}\n"
                + "         .rps_daf5 .x_main-action-cell\n"
                + "         	{float:none!important;\n"
                + "         	margin-right:0!important}\n"
                + "         .rps_daf5 .x_secondary-action-cell\n"
                + "         	{text-align:center;\n"
                + "         	width:100%}\n"
                + "         .rps_daf5 .x_header\n"
                + "         	{margin-top:20px!important;\n"
                + "         	margin-bottom:2px!important}\n"
                + "         .rps_daf5 .x_shop-name__cell\n"
                + "         	{display:block}\n"
                + "         .rps_daf5 .x_order-number__cell\n"
                + "         	{display:block;\n"
                + "         	text-align:left!important;\n"
                + "         	margin-top:20px}\n"
                + "         .rps_daf5 .x_button\n"
                + "         	{width:100%}\n"
                + "         .rps_daf5 .x_or\n"
                + "         	{margin-right:0!important}\n"
                + "         .rps_daf5 .x_apple-wallet-button\n"
                + "         	{text-align:center}\n"
                + "         .rps_daf5 .x_customer-info__item\n"
                + "         	{display:block;\n"
                + "         	width:100%!important}\n"
                + "         .rps_daf5 .x_spacer\n"
                + "         	{display:none}\n"
                + "         .rps_daf5 .x_subtotal-spacer\n"
                + "         	{display:none}\n"
                + "         \n"
                + "         	}\n"
                + "         -->\n"
                + "   </style>\n"
                + "   <div class=\"rps_daf5\">\n"
                + "      <div style=\"margin:0\">\n"
                + "         <table class=\"x_body\" style=\"height:100%!important; width:100%!important; border-spacing:0; border-collapse:collapse\">\n"
                + "            <tbody>\n"
                + "               <tr>\n"
                + "                  <td style=\"\">\n"
                + "                     <table class=\"x_header x_row\" style=\"width:100%; border-spacing:0; border-collapse:collapse; margin:40px 0 20px\">\n"
                + "                        <tbody>\n"
                + "                           <tr>\n"
                + "                              <td class=\"x_header__cell\" style=\"\">\n"
                + "                                 <center>\n"
                + "                                    <table class=\"x_container\" style=\"width:560px; text-align:left; border-spacing:0; border-collapse:collapse; margin:0 auto\">\n"
                + "                                       <tbody>\n"
                + "                                          <tr>\n"
                + "                                             <td style=\"\">\n"
                + "                                                <table class=\"x_row\" style=\"width:100%; border-spacing:0; border-collapse:collapse\">\n"
                + "                                                   <tbody>\n"
                + "                                                      <tr>\n"
                + "                                                         <td class=\"x_shop-name__cell\" style=\"\">\n"
                + "                                                            <h1 class=\"x_shop-name__text\" style=\"font-weight:normal; font-size:30px; color:#333; margin:0 \">HoMED Serviceman System</h1>\n"
                + "                                                         </td>\n"
                + "                                                      </tr>\n"
                + "                                                   </tbody>\n"
                + "                                                </table>\n"
                + "                                             </td>\n"
                + "                                          </tr>\n"
                + "                                       </tbody>\n"
                + "                                    </table>\n"
                + "                                 </center>\n"
                + "                              </td>\n"
                + "                           </tr>\n"
                + "                        </tbody>\n"
                + "                     </table>\n"
                + "                     <table class=\"x_row x_content\" style=\"width:100%; border-spacing:0; border-collapse:collapse\">\n"
                + "                        <tbody>\n"
                + "                           <tr>\n"
                + "                              <td class=\"x_content__cell\" style=\"padding-bottom:40px\">\n"
                + "                                 <center>\n"
                + "                                    <table class=\"x_container\" style=\"width:560px; text-align:left; border-spacing:0; border-collapse:collapse; margin:0 auto\">\n"
                + "                                       <tbody>\n"
                + "                                          <tr>\n"
                + "                                             <td style=\"\">\n"
                + "                                                <h2 style=\"font-weight:normal; font-size:24px; margin:0 0 10px\">Activate your account<h1>OTP: " + otp + "</h1></h2>\n"
                + "                                                <p style=\"color:#777; line-height:150%; font-size:16px; margin:0\">Dear " + serviceman.getName() + ", the admin has created a new account for you. Please proceed to activate your account at the HoMED Serviceman System with the given OTP. Should you encounter any technical difficulties, immediately contact your system admin.</p>\n"
                + "                                                <table class=\"x_row x_actions\" style=\"width:100%; border-spacing:0; border-collapse:collapse; margin-top:20px\">\n"
                + "                                                   <tbody>\n"
                + "                                                      <tr>\n"
                + "                                                         <td class=\"x_empty-line\" style=\"line-height:0.5em\">&nbsp;</td>\n"
                + "                                                      </tr>\n"
                + "                                                      <tr>\n"
                + "                                                         <td class=\"x_actions__cell\" style=\"\">\n"
                + "                                                            <table class=\"x_button x_main-action-cell\" style=\"border-spacing:0; border-collapse:collapse;\">\n"
                + "                                                               <tbody>\n"
                + "                                                                  <tr>\n"
                + "                                                                  </tr>\n"
                + "                                                               </tbody>\n"
                + "                                                            </table>\n"
                + "                                                         </td>\n"
                + "                                                      </tr>\n"
                + "                                                   </tbody>\n"
                + "                                                </table>\n"
                + "                                             </td>\n"
                + "                                          </tr>\n"
                + "                                       </tbody>\n"
                + "                                    </table>\n"
                + "                                 </center>\n"
                + "                              </td>\n"
                + "                           </tr>\n"
                + "                        </tbody>\n"
                + "                     </table>\n"
                + "                     <table class=\"x_row x_footer\" style=\"width:100%; border-spacing:0; border-collapse:collapse; border-top-width:1px; border-top-color:#e5e5e5; border-top-style:solid\">\n"
                + "                        <tbody>\n"
                + "                           <tr>\n"
                + "                              <td class=\"x_footer__cell\" style=\"padding:35px 0\">\n"
                + "                                 <center>\n"
                + "                                    <table class=\"x_container\" style=\"width:560px; text-align:left; border-spacing:0; border-collapse:collapse; margin:0 auto\">\n"
                + "                                       <tbody>\n"
                + "                                          <tr>\n"
                + "                                             <td style=\"\">\n"
                + "                                                <p class=\"x_disclaimer__subtext\" style=\"color:#999; line-height:150%; font-size:14px; margin:0\">Any person receiving this email and any attachment(s) contained, shall treat the information as confidential and not misuse, copy, disclose, distribute or retain the information in any way that amounts to a breach of confidentiality. If you are not the intended recipient, please delete all copies of this email from your computer system. <a href=\"mailto:HoMED.mailer@gmail.com\" target=\"_blank\" rel=\"noopener noreferrer\" data-auth=\"NotApplicable\" style=\"font-size:14px; text-decoration:none; color:#1990C6\">HoMED.mailer@gmail.com</a></p>\n"
                + "                                             </td>\n"
                + "                                          </tr>\n"
                + "                                       </tbody>\n"
                + "                                    </table>\n"
                + "                                 </center>\n"
                + "                              </td>\n"
                + "                           </tr>\n"
                + "                        </tbody>\n"
                + "                     </table>\n"
                + "                  </td>\n"
                + "               </tr>\n"
                + "            </tbody>\n"
                + "         </table>\n"
                + "      </div>\n"
                + "   </div>\n"
                + "</div>";
    }

    private String createEmailBodyEmployeeOtp(Employee employee, String otp) {
        return "<div>\n"
                + "   <style>\n"
                + "      <!--\n"
                + "         .rps_daf5 body\n"
                + "         	{margin:0}\n"
                + "         .rps_daf5 h1 a:hover\n"
                + "         	{font-size:30px;\n"
                + "         	color:#333}\n"
                + "         .rps_daf5 h1 a:active\n"
                + "         	{font-size:30px;\n"
                + "         	color:#333}\n"
                + "         .rps_daf5 h1 a:visited\n"
                + "         	{font-size:30px;\n"
                + "         	color:#333}\n"
                + "         .rps_daf5 a:hover\n"
                + "         	{text-decoration:none}\n"
                + "         .rps_daf5 a:active\n"
                + "         	{text-decoration:none}\n"
                + "         .rps_daf5 a:visited\n"
                + "         	{text-decoration:none}\n"
                + "         .rps_daf5 a:hover\n"
                + "         	{color:#1990C6}\n"
                + "         .rps_daf5 a:active\n"
                + "         	{color:#1990C6}\n"
                + "         .rps_daf5 a:visited\n"
                + "         	{color:#1990C6}\n"
                + "         @media (max-width: 600px) {\n"
                + "         .rps_daf5 .x_container\n"
                + "         	{width:94%!important}\n"
                + "         .rps_daf5 .x_main-action-cell\n"
                + "         	{float:none!important;\n"
                + "         	margin-right:0!important}\n"
                + "         .rps_daf5 .x_secondary-action-cell\n"
                + "         	{text-align:center;\n"
                + "         	width:100%}\n"
                + "         .rps_daf5 .x_header\n"
                + "         	{margin-top:20px!important;\n"
                + "         	margin-bottom:2px!important}\n"
                + "         .rps_daf5 .x_shop-name__cell\n"
                + "         	{display:block}\n"
                + "         .rps_daf5 .x_order-number__cell\n"
                + "         	{display:block;\n"
                + "         	text-align:left!important;\n"
                + "         	margin-top:20px}\n"
                + "         .rps_daf5 .x_button\n"
                + "         	{width:100%}\n"
                + "         .rps_daf5 .x_or\n"
                + "         	{margin-right:0!important}\n"
                + "         .rps_daf5 .x_apple-wallet-button\n"
                + "         	{text-align:center}\n"
                + "         .rps_daf5 .x_customer-info__item\n"
                + "         	{display:block;\n"
                + "         	width:100%!important}\n"
                + "         .rps_daf5 .x_spacer\n"
                + "         	{display:none}\n"
                + "         .rps_daf5 .x_subtotal-spacer\n"
                + "         	{display:none}\n"
                + "         \n"
                + "         	}\n"
                + "         -->\n"
                + "   </style>\n"
                + "   <div class=\"rps_daf5\">\n"
                + "      <div style=\"margin:0\">\n"
                + "         <table class=\"x_body\" style=\"height:100%!important; width:100%!important; border-spacing:0; border-collapse:collapse\">\n"
                + "            <tbody>\n"
                + "               <tr>\n"
                + "                  <td style=\"\">\n"
                + "                     <table class=\"x_header x_row\" style=\"width:100%; border-spacing:0; border-collapse:collapse; margin:40px 0 20px\">\n"
                + "                        <tbody>\n"
                + "                           <tr>\n"
                + "                              <td class=\"x_header__cell\" style=\"\">\n"
                + "                                 <center>\n"
                + "                                    <table class=\"x_container\" style=\"width:560px; text-align:left; border-spacing:0; border-collapse:collapse; margin:0 auto\">\n"
                + "                                       <tbody>\n"
                + "                                          <tr>\n"
                + "                                             <td style=\"\">\n"
                + "                                                <table class=\"x_row\" style=\"width:100%; border-spacing:0; border-collapse:collapse\">\n"
                + "                                                   <tbody>\n"
                + "                                                      <tr>\n"
                + "                                                         <td class=\"x_shop-name__cell\" style=\"\">\n"
                + "                                                            <h1 class=\"x_shop-name__text\" style=\"font-weight:normal; font-size:30px; color:#333; margin:0 \">HoMED Employee Management System</h1>\n"
                + "                                                         </td>\n"
                + "                                                      </tr>\n"
                + "                                                   </tbody>\n"
                + "                                                </table>\n"
                + "                                             </td>\n"
                + "                                          </tr>\n"
                + "                                       </tbody>\n"
                + "                                    </table>\n"
                + "                                 </center>\n"
                + "                              </td>\n"
                + "                           </tr>\n"
                + "                        </tbody>\n"
                + "                     </table>\n"
                + "                     <table class=\"x_row x_content\" style=\"width:100%; border-spacing:0; border-collapse:collapse\">\n"
                + "                        <tbody>\n"
                + "                           <tr>\n"
                + "                              <td class=\"x_content__cell\" style=\"padding-bottom:40px\">\n"
                + "                                 <center>\n"
                + "                                    <table class=\"x_container\" style=\"width:560px; text-align:left; border-spacing:0; border-collapse:collapse; margin:0 auto\">\n"
                + "                                       <tbody>\n"
                + "                                          <tr>\n"
                + "                                             <td style=\"\">\n"
                + "                                                <h2 style=\"font-weight:normal; font-size:24px; margin:0 0 10px\">Activate your account<h1>OTP: " + otp + "</h1></h2>\n"
                + "                                                <p style=\"color:#777; line-height:150%; font-size:16px; margin:0\">Dear " + employee.getName() + ", the admin has created a new account for you. Please proceed to activate your account at the HoMED Employee Management System with the given OTP. Should you encounter any technical difficulties, immediately contact your system admin.</p>\n"
                + "                                                <table class=\"x_row x_actions\" style=\"width:100%; border-spacing:0; border-collapse:collapse; margin-top:20px\">\n"
                + "                                                   <tbody>\n"
                + "                                                      <tr>\n"
                + "                                                         <td class=\"x_empty-line\" style=\"line-height:0.5em\">&nbsp;</td>\n"
                + "                                                      </tr>\n"
                + "                                                      <tr>\n"
                + "                                                         <td class=\"x_actions__cell\" style=\"\">\n"
                + "                                                            <table class=\"x_button x_main-action-cell\" style=\"border-spacing:0; border-collapse:collapse;\">\n"
                + "                                                               <tbody>\n"
                + "                                                                  <tr>\n"
                + "                                                                     <td class=\"x_button__cell\" align=\"center\" bgcolor=\"#1990C6\" style=\"border-radius:4px\"><a href=\"" + linkToHomed + "\" target=\"_blank\" rel=\"noopener noreferrer\" data-auth=\"NotApplicable\" class=\"x_button__text\" style=\"font-size:16px; text-decoration:none; display:block; color:#fff; padding:30px 25px\">Go To HoMED</a></td>\n"
                + "                                                                  </tr>\n"
                + "                                                               </tbody>\n"
                + "                                                            </table>\n"
                + "                                                         </td>\n"
                + "                                                      </tr>\n"
                + "                                                   </tbody>\n"
                + "                                                </table>\n"
                + "                                             </td>\n"
                + "                                          </tr>\n"
                + "                                       </tbody>\n"
                + "                                    </table>\n"
                + "                                 </center>\n"
                + "                              </td>\n"
                + "                           </tr>\n"
                + "                        </tbody>\n"
                + "                     </table>\n"
                + "                     <table class=\"x_row x_footer\" style=\"width:100%; border-spacing:0; border-collapse:collapse; border-top-width:1px; border-top-color:#e5e5e5; border-top-style:solid\">\n"
                + "                        <tbody>\n"
                + "                           <tr>\n"
                + "                              <td class=\"x_footer__cell\" style=\"padding:35px 0\">\n"
                + "                                 <center>\n"
                + "                                    <table class=\"x_container\" style=\"width:560px; text-align:left; border-spacing:0; border-collapse:collapse; margin:0 auto\">\n"
                + "                                       <tbody>\n"
                + "                                          <tr>\n"
                + "                                             <td style=\"\">\n"
                + "                                                <p class=\"x_disclaimer__subtext\" style=\"color:#999; line-height:150%; font-size:14px; margin:0\">Any person receiving this email and any attachment(s) contained, shall treat the information as confidential and not misuse, copy, disclose, distribute or retain the information in any way that amounts to a breach of confidentiality. If you are not the intended recipient, please delete all copies of this email from your computer system. <a href=\"mailto:HoMED.mailer@gmail.com\" target=\"_blank\" rel=\"noopener noreferrer\" data-auth=\"NotApplicable\" style=\"font-size:14px; text-decoration:none; color:#1990C6\">HoMED.mailer@gmail.com</a></p>\n"
                + "                                             </td>\n"
                + "                                          </tr>\n"
                + "                                       </tbody>\n"
                + "                                    </table>\n"
                + "                                 </center>\n"
                + "                              </td>\n"
                + "                           </tr>\n"
                + "                        </tbody>\n"
                + "                     </table>\n"
                + "                  </td>\n"
                + "               </tr>\n"
                + "            </tbody>\n"
                + "         </table>\n"
                + "      </div>\n"
                + "   </div>\n"
                + "</div>";
    }
}
