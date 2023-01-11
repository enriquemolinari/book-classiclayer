package layer.main;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

public class SetUpDatabase {

  private String url;

  public SetUpDatabase(String connUrl) {
    this.url = connUrl;
  }

  public void start() {

    Jdbi jdbi = Jdbi.create(this.url);

    jdbi.useHandle(handle -> {

      handle.execute("CREATE TABLE movie (id_movie INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255), id_cover_image varchar(10), duration INT)");

      handle.execute("CREATE TABLE theatre (id_theatre INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255))");

      handle.execute("CREATE TABLE show (id_show INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, id_theatre INT not null, "
          + "start_time timestamp not null, "
          + "foreign key (id_movie) references movie, "
          + "foreign key (id_theatre) references theatre)");

      handle.execute("CREATE TABLE seat (id_seat INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_theatre INT not null, number INT not null, "
          + "foreign key (id_theatre) references theatre)");

      handle.execute("CREATE TABLE person (id_person INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255), surname VARCHAR(255), "
          + "email VARCHAR(255))");

      handle.execute("CREATE TABLE movie_cast (id_movie_cast INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, id_person INT not null, character_name VARCHAR(255), "
          + "foreign key (id_person) references person, foreign key (id_movie) references movie)");

      handle.execute("CREATE TABLE users (id_user INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "username VARCHAR(255), password VARCHAR(255), "
          + "id_person INT not null, " + "points INT not null, "
          + "foreign key (id_person) references person)");

      handle.execute("CREATE TABLE users_audit (id_user_audit INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "login_date timestamp not null, id_user INT not null, "
          + "foreign key (id_user) references users)");

      handle.execute(
          "CREATE TABLE booking (id_show INT not null, id_seat INT not null, "
              + "id_user int, reserved boolean, reserved_until timestamp, confirmed boolean, "
              + "foreign key (id_show) references show, "
              + "foreign key (id_seat) references seat, "
              + "primary key (id_show, id_seat))");

      handle
          .execute("CREATE TABLE rating_detail (id_rating_detail INT NOT NULL "
              + "primary key generated always as identity (start with 1,increment by 1), "
              + "id_movie INT not null, id_user INT not null, "
              + "comment VARCHAR(500), value decimal(2,1) not null, "
              + "foreign key (id_movie) references movie, "
              + "foreign key (id_user) references users)");

      handle.execute("CREATE TABLE rating (id_rating INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, value decimal(2,1) not null, "
          + "foreign key (id_movie) references movie)");

      // Movies

      long idSchoolMovie = handle.createUpdate(
          "INSERT INTO movie (name, id_cover_image, duration) VALUES (?, ?, ?)")
          .bind(0, "Rock in the School").bind(1, "rockschool").bind(2, "109")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idFishMovie = handle.createUpdate(
          "INSERT INTO movie (name, id_cover_image, duration) VALUES (?, ?, ?)")
          .bind(0, "Small Fish").bind(1, "smallfish").bind(2, "125")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      // People

      long idJake = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "jake").bind(1, "White").bind(2, "jake@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idJosh = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Josh").bind(1, "Blue").bind(2, "josh@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idNervan = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Nervan").bind(1, "Allister").bind(2, "nervan@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idErnest = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Ernest").bind(1, "Finey").bind(2, "ernest@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idEnrique = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Enrique").bind(1, "Molinari")
          .bind(2, "enrique.molinari@gmail.com").executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      long idJosefina = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Josefina").bind(1, "Simini").bind(2, "jsimini@movies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idLucia = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Lucia").bind(1, "Molimini").bind(2, "lu@movies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idNico = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Nico").bind(1, "Molimini").bind(2, "nico@movies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      // Movie Cast

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idJake).bind(1, idSchoolMovie).bind(2, "Dewey Finn")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idJosh).bind(1, idSchoolMovie).bind(2, "Ned Schneebly")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idErnest).bind(1, idFishMovie).bind(2, "Ed Bloom (senior)")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idNervan).bind(1, idFishMovie).bind(2, "Ed Bloom (young)")
          .execute();

      // Theatre

      long idTheatreA =
          handle.createUpdate("INSERT INTO theatre (name) VALUES (?)")
              .bind(0, "Theatre A").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      long idTheatreB =
          handle.createUpdate("INSERT INTO theatre (name) VALUES (?)")
              .bind(0, "Theatre B").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      // show 1
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time) VALUES (?, ?, ?)")
          .bind(0, idFishMovie).bind(1, idTheatreA)
          .bind(2, LocalDateTime.now().plusDays(2)).execute();

      // show 2
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time) VALUES (?, ?, ?)")
          .bind(0, idFishMovie).bind(1, idTheatreA)
          .bind(2, LocalDateTime.now().plusDays(3)).execute();

      // show 3
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time) VALUES (?, ?, ?)")
          .bind(0, idSchoolMovie).bind(1, idTheatreB)
          .bind(2, LocalDateTime.now().plusDays(1)).execute();

      // show 4
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time) VALUES (?, ?, ?)")
          .bind(0, idSchoolMovie).bind(1, idTheatreB)
          .bind(2, LocalDateTime.now().plusDays(1).plusHours(5)).execute();

      // Seats from Theatre A

      PreparedBatch batchSeatsA = handle
          .prepareBatch("INSERT INTO seat (id_theatre, number) VALUES(?, ?)");
      for (int i = 1; i <= 30; i++) {
        batchSeatsA.bind(0, idTheatreA).bind(1, i).add();
      }
      batchSeatsA.execute();

      // Seats from Theatre B

      PreparedBatch batchSeatsB = handle
          .prepareBatch("INSERT INTO seat (id_theatre, number) VALUES(?, ?)");
      for (int i = 1; i <= 50; i++) {
        batchSeatsB.bind(0, idTheatreB).bind(1, i).add();
      }
      batchSeatsB.execute();

      // Bookings

      List<Map<String, Object>> shows =
          handle.createQuery("SELECT id_show, id_theatre from show").mapToMap()
              .list();

      // show 1 and 2 in TheatreA (30 seats)
      // show 3 and 4 in TheatreB (50 seats)

      for (Map<String, Object> map : shows) {
        List<Long> seats =
            handle.select("SELECT id_seat from seat where id_theatre = ?",
                map.get("id_theatre")).mapTo(Long.class).list();

        PreparedBatch bookings = handle.prepareBatch(
            "INSERT INTO booking (id_show, id_seat, id_user, reserved, confirmed, reserved_until) "
                + "VALUES(?, ?, null, false, false, null)");
        for (Long s : seats) {
          bookings.bind(0, map.get("id_show")).bind(1, s).add();
        }
        bookings.execute();
      }

      // Users

      handle.createUpdate(
          "INSERT INTO users (id_person, username, password, points) VALUES (?, ?, ?, 0)")
          .bind(0, idEnrique).bind(1, "emolinari").bind(2, "123").execute();

      handle.createUpdate(
          "INSERT INTO users (id_person, username, password, points) VALUES (?, ?, ?, 0)")
          .bind(0, idJosefina).bind(1, "jsimini").bind(2, "123").execute();

      handle.createUpdate(
          "INSERT INTO users (id_person, username, password, points) VALUES (?, ?, ?, 0)")
          .bind(0, idLucia).bind(1, "lucia").bind(2, "123").execute();

      handle.createUpdate(
          "INSERT INTO users (id_person, username, password, points) VALUES (?, ?, ?, 0)")
          .bind(0, idNico).bind(1, "nico").bind(2, "123").execute();

      // rating

      handle.createUpdate(
          "INSERT INTO rating_detail (id_movie, id_user, comment, value) VALUES (?, ?, ?, ?)")
          .bind(0, idSchoolMovie).bind(1, 1).bind(2, "Great movie!")
          .bind(3, new BigDecimal(4.5)).execute();

      handle.createUpdate("INSERT INTO rating (id_movie, value) VALUES (?, ?)")
          .bind(0, idSchoolMovie).bind(1, new BigDecimal(4.5)).execute();

      handle.createUpdate(
          "INSERT INTO rating_detail (id_movie, id_user, comment, value) VALUES (?, ?, ?, ?)")
          .bind(0, idFishMovie).bind(1, 1).bind(2, "Fantastic movie!!")
          .bind(3, new BigDecimal(5)).execute();

      handle.createUpdate("INSERT INTO rating (id_movie, value) VALUES (?, ?)")
          .bind(0, idFishMovie).bind(1, new BigDecimal(5)).execute();

    });

    // jdbi.useHandle(handle -> {
    // List<Map<String, Object>> map =
    // handle.createQuery("SELECT * FROM seat").mapToMap().list();
    //
    // for (Map<String, Object> map2 : map) {
    // System.out.println(map2.get("id_theatre") + "->" + map2.get("id_seat")
    // + "->" + map2.get("number"));
    // }
    // });

    // jdbi.useHandle(handle -> {
    // List<Map<String, Object>> map =
    // handle.createQuery("SELECT * FROM booking").mapToMap().list();
    //
    // for (Map<String, Object> map2 : map) {
    // System.out.println(map2.get("id_show") + "->" + map2.get("id_seat")
    // + "->" + map2.get("reserved"));
    // }
    // });

  }
}
