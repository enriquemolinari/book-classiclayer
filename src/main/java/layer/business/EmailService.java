package layer.business;

public interface EmailService {

  void send(String emailTo, String subject, String message);
}
