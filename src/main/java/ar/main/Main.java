package ar.main;

import java.util.List;
import org.jdbi.v3.core.Jdbi;

public class Main {

  public static void main(String[] args) {

    Jdbi jdbi = Jdbi.create("jdbc:derby:memory:cinemacenter;create=true"); // (H2 in-memory database)

    List<User> users = jdbi.withHandle(handle -> {
      handle.execute(
          "CREATE TABLE \"user\" (id INT NOT NULL, \"name\" VARCHAR(255))");

      handle.execute("INSERT INTO \"user\" (id, \"name\") VALUES (?, ?)", 0,
          "Alice");

      handle.createUpdate("INSERT INTO \"user\" (id, \"name\") VALUES (?, ?)")
          .bind(0, 1) // 0-based parameter indexes
          .bind(1, "Bob").execute();

      handle
          .createUpdate(
              "INSERT INTO \"user\" (id, \"name\") VALUES (:id, :name)")
          .bind("id", 2).bind("name", "Clarice").execute();

      return handle.createQuery("SELECT * FROM \"user\" ORDER BY \"name\"")
          .map((rs, ctx) -> new User(rs.getInt("id"), rs.getString("name")))
          .list();
    });

    for (User user : users) {
      System.out.println(user);
    }

  }
}
