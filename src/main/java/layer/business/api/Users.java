package layer.business.api;

public interface Users {

  UserRecord login(String username, String password);

  Long userIdFrom(String token) throws UnauthorizedException;

}
