package layer.web;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import layer.business.DefaultCinemaShows;
import layer.business.DefaultMovies;
import layer.business.DefaultUsers;
import layer.business.EmailService;
import layer.business.PasetoToken;
import layer.business.SomePaymentProvider;
import layer.data.JdbiMoviesDataService;
import layer.data.JdbiRatingDataService;
import layer.data.JdbiShowsDataService;
import layer.data.JdbiUserAuthDataService;
import layer.main.SetUpDatabase;

public class TestMovieApis {

  private static Web web;

  @BeforeAll
  public static void beforeAll() {
    var connStr = "jdbc:hsqldb:mem;create=true";

    new SetUpDatabase(connStr).start();

    String secret = "bfhAp4qdm92bD0FIOZLanC66KgCS8cYVxq/KlSVdjhI=";

    var jdbi = Jdbi.create(connStr);
    var movies = new DefaultMovies(new JdbiMoviesDataService(jdbi),
        new JdbiRatingDataService(jdbi), new JdbiUserAuthDataService(jdbi));
    var cinema = new DefaultCinemaShows(new JdbiShowsDataService(jdbi),
        new JdbiUserAuthDataService(jdbi), new EmailService() {
          @Override
          public void send(String emailTo, String subject, String message) {
            // Do not send emails in functional tests
          }
        }, new SomePaymentProvider());
    var users = new DefaultUsers(new JdbiUserAuthDataService(jdbi),
        new PasetoToken(secret));

    web = new Web("client-url", 8889, movies, cinema, users);
    web.start();
  }

  @Test
  public void testMovieDetail() {
    get("http://localhost:8889/movies/1").then()
        .body("movie", hasKey("directorName")).body("movie", hasKey("coverImg"))
        .rootPath("movie").body("name", equalTo("Rock in the School"))
        .body("duration", equalTo("1hr 49mins"))
        .body("genres", hasItems("Comedy", "Crime"))
        .body("cast.name", hasItems("Jake", "Josh"));
  }

  @Test
  public void testMovies() {
    get("http://localhost:8889/movies").then().rootPath("movies").body("name",
        hasItems("Rock in the School", "Small Fish", "Crash Tea",
            "Running far away"));
  }

  @Test
  public void testRateNotLoggedIn() {
    given().contentType("application/json")
        .body("{\n" + "    \"value\": 5,\n"
            + "    \"comment\": \"Another comment ...\"\n" + "}")
        .post("http://localhost:8889/movies/{id}/rate", "1").then()
        .body("result", equalTo("error"))
        .body("message", equalTo("Invalid token. You have to login."));
  }

  @Test
  public void testRateAlreadyVoted() {
    String token = given().contentType("application/json")
        .body("{\n" + "    \"username\": \"emolinari\",\n"
            + "    \"password\": \"123\"\n" + "}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json")
        .body("{\"value\": 5,\n" + "    \"comment\": \"Another comment ...\"\n"
            + "}")
        .cookie("token", token)
        .post("http://localhost:8889/movies/{id}/rate", "1").then()
        .body("result", equalTo("error"));

    get("http://localhost:8889/movies/{id}/rate", 1).then().body("rating.total",
        equalTo(1));
  }

  @Test
  public void testRateOk() {
    String token = given().contentType("application/json")
        .body("{\n" + "    \"username\": \"jsimini\",\n"
            + "    \"password\": \"123\"\n" + "}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json")
        .body("{\"value\": 5,\n" + "    \"comment\": \"Another comment ...\"\n"
            + "}")
        .cookie("token", token)
        .post("http://localhost:8889/movies/{id}/rate", "1").then()
        .body("result", equalTo("success"));

    get("http://localhost:8889/movies/{id}/rate", 1).then().body("rating.total",
        equalTo(2));
  }

  @AfterAll
  public static void afterAll() {
    web.close();
  }

}
