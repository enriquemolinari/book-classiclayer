package layer.business.api;

public class CinemaException extends RuntimeException {

  public CinemaException(RuntimeException e, String msg) {
    super(msg, e);
  }

  public CinemaException(String msg) {
    super(msg);
  }
}
