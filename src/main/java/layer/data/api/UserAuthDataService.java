package layer.data.api;

import java.util.Optional;

public interface UserAuthDataService {

  Optional<UserData> login(String username, String password);
}
