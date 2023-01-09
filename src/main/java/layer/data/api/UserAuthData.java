package layer.data.api;

import java.util.Optional;

public interface UserAuthData {

  Optional<UserRecord> login(String username, String password);
}
