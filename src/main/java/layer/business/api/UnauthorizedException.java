package layer.business.api;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String msg) {
    super(msg);
  }

}
