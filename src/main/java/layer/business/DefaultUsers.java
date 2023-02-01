package layer.business;

import java.util.Map;
import layer.business.api.UnauthorizedException;
import layer.business.api.UserRecord;
import layer.business.api.Users;
import layer.business.api.UsersException;
import layer.data.api.UserAuthDataService;

public class DefaultUsers implements Users {

  private UserAuthDataService usersData;
  private Token token;

  public DefaultUsers(UserAuthDataService usersData, Token token) {
    this.usersData = usersData;
    this.token = token;
  }

  @Override
  public UserRecord login(String username, String password) {
    var udata = usersData.login(username, password);
    if (udata.isEmpty()) {
      throw new UsersException("Invalid username or password");
    }

    String base64Token = token.token(
        Map.of("username", udata.get().userName(), "id", udata.get().id()));

    return new UserRecord(udata.get().id(), udata.get().userName(),
        udata.get().points(), base64Token);
  }

  @Override
  public Long userIdFrom(String token) throws UnauthorizedException {
    return this.token.userIdFrom(token);
  }
}
