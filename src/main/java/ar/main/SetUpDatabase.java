package ar.main;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

public class SetUpDatabase {

  public void setUp() {

    // memory
    // Jdbi jdbi = Jdbi.create("jdbc:derby:memory:mycinema;create=true");

    // disk
    Jdbi jdbi = Jdbi.create("jdbc:derby:/home/enrique/mycinema;create=true");

    jdbi.useHandle(handle -> {

      handle.execute("CREATE TABLE movie (id_movie INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255), duration INT)");

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

      handle.execute(
          "CREATE TABLE ranking_detail (id_ranking_detail INT NOT NULL "
              + "primary key generated always as identity (start with 1,increment by 1), "
              + "id_movie INT not null, id_user INT not null, "
              + "value decimal not null, "
              + "foreign key (id_movie) references movie, "
              + "foreign key (id_user) references users)");

      handle.execute("CREATE TABLE ranking (id_ranking INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, value decimal not null, "
          + "foreign key (id_movie) references movie)");

      // Movies

      long idSchoolMovie = handle
          .createUpdate("INSERT INTO movie (name, duration) VALUES (?, ?)")
          .bind(0, "School of Rock").bind(1, "109")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idBigFishMovie = handle
          .createUpdate("INSERT INTO movie (name, duration) VALUES (?, ?)")
          .bind(0, "Big Fish").bind(1, "125").executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      // People

      long idJackBlack = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Jack").bind(1, "Black").bind(2, "jack.black@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idMikeWhite = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Mike").bind(1, "White").bind(2, "mike.white@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idEwan = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Ewan").bind(1, "McGregor")
          .bind(2, "ewan.mcgregor@mymovies.com").executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      long idAlbert = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Albert").bind(1, "Finney")
          .bind(2, "albert.finney@mymovies.com").executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      long idEnrique = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Enrique").bind(1, "Molinari")
          .bind(2, "enrique.molinari@gmail.com").executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      // Movie Cast

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idJackBlack).bind(1, idSchoolMovie).bind(2, "Dewey Finn")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idMikeWhite).bind(1, idSchoolMovie).bind(2, "Ned Schneebly")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idAlbert).bind(1, idBigFishMovie)
          .bind(2, "Ed Bloom (senior)").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name) VALUES (?, ?, ?)")
          .bind(0, idEwan).bind(1, idBigFishMovie).bind(2, "Ed Bloom (young)")
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
          .bind(0, idBigFishMovie).bind(1, idTheatreA)
          .bind(2, LocalDateTime.now().plusDays(2)).execute();

      // show 2
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time) VALUES (?, ?, ?)")
          .bind(0, idBigFishMovie).bind(1, idTheatreA)
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
