package layer.main;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

class SetUpDatabase {

  private String url;

  public SetUpDatabase(String connUrl) {
    this.url = connUrl;
  }

  public void start() {

    Jdbi jdbi = Jdbi.create(this.url);

    jdbi.useTransaction(handle -> {

      handle.execute("DROP SCHEMA PUBLIC CASCADE");

      handle.execute("CREATE TABLE movie (id_movie INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255), release_date timestamp not null, age_restriction int, "
          + "id_cover_image varchar(10), duration INT, plot varchar(750))");

      handle.execute("CREATE TABLE genre (id_genre INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "description VARCHAR(255))");

      handle.execute("CREATE TABLE movie_genre (id_movie_genre INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie int, id_genre int,"
          + "foreign key (id_movie) references movie,"
          + "foreign key (id_genre) references genre)");

      handle.execute("CREATE TABLE theatre (id_theatre INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255))");

      handle.execute("CREATE TABLE show (id_show INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, id_theatre INT not null, "
          + "start_time timestamp not null, price decimal(4,2), "
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
          + "cast_type CHAR(1), " // D = Director, A = Actor/Actress, W = Writer, C = Crew
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

      handle.execute("CREATE TABLE sale (id_sale INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_show INT not null, amount decimal(6,2), id_user INT not null, "
          + "payed_at timestamp not null, "
          + "foreign key (id_show) references show, "
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
              + "comment VARCHAR(500), value tinyint not null, "
              + "created_at timestamp not null, "
              + "foreign key (id_movie) references movie, "
              + "foreign key (id_user) references users)");

      handle.execute("CREATE TABLE rating (id_rating INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, value decimal(2,1) not null, "
          + "foreign key (id_movie) references movie)");

      // Genres

      long idGenre1 =
          handle.createUpdate("INSERT INTO genre (description) VALUES (?)")
              .bind(0, "Comedy").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      long idGenre2 =
          handle.createUpdate("INSERT INTO genre (description) VALUES (?)")
              .bind(0, "Crime").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      long idGenre3 =
          handle.createUpdate("INSERT INTO genre (description) VALUES (?)")
              .bind(0, "Drama").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      long idGenre4 =
          handle.createUpdate("INSERT INTO genre (description) VALUES (?)")
              .bind(0, "Thriller").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      long idGenre5 =
          handle.createUpdate("INSERT INTO genre (description) VALUES (?)")
              .bind(0, "Mystery").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      long idGenre6 =
          handle.createUpdate("INSERT INTO genre (description) VALUES (?)")
              .bind(0, "Action").executeAndReturnGeneratedKeys()
              .mapTo(Long.class).one();

      // Movies

      long idSchoolMovie = handle.createUpdate(
          "INSERT INTO movie (name, id_cover_image, duration, plot, release_date, age_restriction) "
              + "VALUES (?, ?, ?, ?, ?, ?)")
          .bind(0, "Rock in the School").bind(1, "movie1-424").bind(2, "109")
          .bind(3,
              "A teacher tries to teach Rock & Roll music and history to elementary school kids.")
          .bind(4, LocalDate.now().minusDays(3)).bind(5, 13)
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idFishMovie = handle.createUpdate(
          "INSERT INTO movie (name, id_cover_image, duration, plot, release_date, age_restriction) "
              + "VALUES (?, ?, ?, ?, ?, ?)")
          .bind(0, "Small Fish").bind(1, "movie2-424").bind(2, "125")
          .bind(3, "A caring father teaches life values while fishing.")
          .bind(4, LocalDate.now().minusDays(2)).bind(5, 18)
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idMovie3 = handle.createUpdate(
          "INSERT INTO movie (name, id_cover_image, duration, plot, release_date, age_restriction) "
              + "VALUES (?, ?, ?, ?, ?, ?)")
          .bind(0, "Crash Tea").bind(1, "movie3-424").bind(2, "105")
          .bind(3, "A documentary about tea.")
          .bind(4, LocalDate.now().minusDays(1)).bind(5, 13)
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idMovie4 = handle.createUpdate(
          "INSERT INTO movie (name, id_cover_image, duration, plot, release_date, age_restriction) "
              + "VALUES (?, ?, ?, ?, ?, ?)")
          .bind(0, "Running far away").bind(1, "movie4-424").bind(2, "105")
          .bind(3,
              "José a sad person run away from his town looking for new adventures.")
          .bind(4, LocalDate.now()).bind(5, 6).executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      // movies and genres

      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idSchoolMovie).bind(1, idGenre1).execute();

      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idSchoolMovie).bind(1, idGenre2).execute();

      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idFishMovie).bind(1, idGenre3).execute();

      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idFishMovie).bind(1, idGenre4).execute();

      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idFishMovie).bind(1, idGenre5).execute();


      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idMovie3).bind(1, idGenre5).execute();

      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idMovie3).bind(1, idGenre2).execute();


      handle
          .createUpdate(
              "INSERT INTO movie_genre (id_movie, id_genre) VALUES (?, ?)")
          .bind(0, idMovie4).bind(1, idGenre1).execute();


      // People

      long idJake = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Jake").bind(1, "White").bind(2, "jake@mymovies.com")
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

      long idCast1 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Camilo").bind(1, "Fernandez").bind(2, "camilo@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idCast2 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Franco").bind(1, "Elchow").bind(2, "franco@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idCast3 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Michael").bind(1, "Martinez")
          .bind(2, "michael@mymovies.com").executeAndReturnGeneratedKeys()
          .mapTo(Long.class).one();

      long idCast4 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Michel").bind(1, "Orenson").bind(2, "michel@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idDir1 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Craig").bind(1, "Wagemen").bind(2, "craig@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idDir2 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Judith").bind(1, "Zavele").bind(2, "judith@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idDir3 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Andre").bind(1, "Lambert").bind(2, "andre@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      long idDir4 = handle
          .createUpdate(
              "INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
          .bind(0, "Colin").bind(1, "Clifferd").bind(2, "colin@mymovies.com")
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      // Directors

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, cast_type) VALUES (?, ?, ?)")
          .bind(0, idDir1).bind(1, idSchoolMovie).bind(2, "D").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, cast_type) VALUES (?, ?, ?)")
          .bind(0, idDir2).bind(1, idFishMovie).bind(2, "D").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, cast_type) VALUES (?, ?, ?)")
          .bind(0, idDir3).bind(1, idMovie3).bind(2, "D").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, cast_type) VALUES (?, ?, ?)")
          .bind(0, idDir4).bind(1, idMovie4).bind(2, "D").execute();

      // Movie Cast

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idJake).bind(1, idSchoolMovie).bind(2, "Daniel Finne")
          .bind(3, "A").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idJosh).bind(1, idSchoolMovie).bind(2, "Norber Carl")
          .bind(3, "A").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idErnest).bind(1, idFishMovie)
          .bind(2, "Edward Blomsky (senior)").bind(3, "A").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idNervan).bind(1, idFishMovie)
          .bind(2, "Edward Blomsky (young)").bind(3, "A").execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idCast1).bind(1, idMovie3).bind(2, "Judy").bind(3, "A")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idCast2).bind(1, idMovie3).bind(2, "George").bind(3, "A")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idCast3).bind(1, idMovie4).bind(2, "Mike").bind(3, "A")
          .execute();

      handle.createUpdate(
          "INSERT INTO movie_cast (id_person, id_movie, character_name, cast_type) VALUES (?, ?, ?, ?)")
          .bind(0, idCast4).bind(1, idMovie4).bind(2, "Teressa").bind(3, "A")
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
          "INSERT INTO show (id_movie, id_theatre, start_time, price) VALUES (?, ?, ?, ?)")
          .bind(0, idFishMovie).bind(1, idTheatreA)
          .bind(2, LocalDateTime.now().plusDays(1).plusHours(1))
          .bind(3, new BigDecimal(21)).execute();

      // show 2
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time, price) VALUES (?, ?, ?, ?)")
          .bind(0, idFishMovie).bind(1, idTheatreA)
          .bind(2, LocalDateTime.now().plusDays(1).plusHours(4))
          .bind(3, new BigDecimal(21)).execute();

      // show 3
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time, price) VALUES (?, ?, ?, ?)")
          .bind(0, idSchoolMovie).bind(1, idTheatreB)
          .bind(2, LocalDateTime.now().plusDays(2).plusHours(1))
          .bind(3, new BigDecimal(19)).execute();

      // show 4
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time, price) VALUES (?, ?, ?, ?)")
          .bind(0, idSchoolMovie).bind(1, idTheatreB)
          .bind(2, LocalDateTime.now().plusDays(2).plusHours(5))
          .bind(3, new BigDecimal(19)).execute();

      // show 5
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time, price) VALUES (?, ?, ?, ?)")
          .bind(0, idMovie3).bind(1, idTheatreA)
          .bind(2, LocalDateTime.now().plusDays(2).plusHours(2))
          .bind(3, new BigDecimal(19)).execute();

      // show 5
      handle.createUpdate(
          "INSERT INTO show (id_movie, id_theatre, start_time, price) VALUES (?, ?, ?, ?)")
          .bind(0, idMovie4).bind(1, idTheatreB)
          .bind(2, LocalDateTime.now().plusHours(2)).bind(3, new BigDecimal(19))
          .execute();

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
          "INSERT INTO rating_detail (id_movie, id_user, comment, value, created_at) VALUES (?, ?, ?, ?, ?)")
          .bind(0, idSchoolMovie).bind(1, 1).bind(2, "Great movie!").bind(3, 4)
          .bind(4, LocalDateTime.now().minusDays(5)).execute();

      handle.createUpdate("INSERT INTO rating (id_movie, value) VALUES (?, ?)")
          .bind(0, idSchoolMovie).bind(1, new BigDecimal(4)).execute();

      handle.createUpdate(
          "INSERT INTO rating_detail (id_movie, id_user, comment, value, created_at) VALUES (?, ?, ?, ?, ?)")
          .bind(0, idFishMovie).bind(1, 1).bind(2, "Fantastic movie!!")
          .bind(3, 5).bind(4, LocalDateTime.now().minusDays(4)).execute();

      handle.createUpdate("INSERT INTO rating (id_movie, value) VALUES (?, ?)")
          .bind(0, idFishMovie).bind(1, new BigDecimal(5)).execute();

    });
  }
}
