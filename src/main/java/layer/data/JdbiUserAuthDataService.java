package layer.data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.FullUserData;
import layer.data.api.UserAuthDataService;
import layer.data.api.UserData;

public class JdbiUserAuthDataService implements UserAuthDataService {

  private Jdbi jdbi;

  public JdbiUserAuthDataService(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Optional<UserData> login(String username, String password) {
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
          .of(new UserData(Long.valueOf(user.get().get("id_user").toString()),
              user.get().get("username").toString(),
              Integer.valueOf(user.get().get("points").toString())));
    });
  }

  @Override
  public FullUserData details(Long idUser) {
    return jdbi.withHandle(handle -> {
      var maybeUser = handle.createQuery(
          "SELECT id_user, username, points, name, surname, email from users u, person p "
              + "where p.id_person = u.id_person and id_user = :iduser")
          .bind("iduser", idUser).mapToMap().findOne();

      return maybeUser.map(user -> new FullUserData(
          new UserData(Long.valueOf(user.get("id_user").toString()),
              user.get("username").toString(),
              Integer.valueOf(user.get("points").toString())),
          user.get("name").toString(), user.get("surname").toString(),
          user.get("email").toString())).get();

    });

  }
}
