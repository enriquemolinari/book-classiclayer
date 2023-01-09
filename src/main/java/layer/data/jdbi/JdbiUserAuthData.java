package layer.data.jdbi;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.UserAuthData;
import layer.data.api.UserRecord;

public class JdbiUserAuthData implements UserAuthData {

  private Jdbi jdbi;

  public JdbiUserAuthData(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Optional<UserRecord> login(String username, String password) {
    Objects.requireNonNull(username, "username must not be null");
    Objects.requireNonNull(password, "password must not be null");

    return jdbi.withHandle(handle -> {
      Optional<Map<String, Object>> user = handle.createQuery(
          "SELECT id_user, username, points from users where username = :user and password = :pass")
          .bind("user", username).bind("pass", password).mapToMap().findOne();

      if (user.isEmpty()) {
        return Optional.empty();
      }

      handle.createUpdate(
          "INSERT INTO users_audit(login_date, id_user) values (:date, :id_user)")
          .bind("date", LocalDateTime.now())
          .bind("id_user", user.get().get("id_user")).execute();

      return Optional
          .of(new UserRecord(Long.valueOf(user.get().get("id_user").toString()),
              user.get().get("username").toString(),
              Integer.valueOf(user.get().get("points").toString())));
    });
  }
}
