package layer.business;

import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import layer.business.api.CinemaException;

public class MailTrapEmailService implements EmailService {

  private static final String PORT = "2525";
  private static final String HOST = "smtp.mailtrap.io";
  private String emailFrom;
  private String username;
  private String password;

  public MailTrapEmailService(String user, String pwd, String emailFrom) {
    this.emailFrom = emailFrom;
    this.username = user;
    this.password = pwd;
  }

  @Override
  public void send(String emailTo, String subject, String msg) {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", HOST);
    props.put("mail.smtp.port", PORT);

    Session session =
        Session.getInstance(props, new jakarta.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(this.emailFrom));
      message.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(emailTo));
      message.setSubject(subject);
      message.setText(msg);

      // Send message
      Transport.send(message);
    } catch (MessagingException e) {
      throw new CinemaException(new RuntimeException(e),
          "The email could not be sent");
    }
  }
}
