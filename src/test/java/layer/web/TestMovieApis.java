package layer.web;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestMovieApis {

  private static SetUp webApp;

  @BeforeAll
  public static void beforeAll() {
    webApp = new SetUp();
    webApp.testEnvSetUp();
  }


  @Test
  public void testMovieDetail() {
    get("http://localhost:8889/movies/1").then()
        .body("movie", hasKey("directorName")).and()
        .body("movie", hasKey("coverImg")).rootPath("movie").and()
        .body("name", equalTo("Rock in the School"))
        .body("duration", equalTo("1hr 49mins")).and()
        .body("genres", hasItems("Comedy", "Crime")).and()
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
        .body("result", equalTo("error")).and()
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
  public void testRetrieveRate() {
    get("http://localhost:8889/movies/1/rate").then()
        .body("result", equalTo("success")).and()
        .body("rating", hasKey("details")).and()
        .body("rating.total", equalTo(2)).and()
        .body("rating.details.username", hasItems("emolinari"))
        .body("rating.details.vote", hasItems(4));
  }

  @Test
  public void testRateOk() {
    String token = given().contentType("application/json")
        .body("{\"username\": \"jsimini\",\n" + "    \"password\": \"123\"\n"
            + "}")
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

  @Test
  public void testRateWithExpiredToken() {
    String token =
        "v2.local.KzZ-dO-hdFPpXSW3AD78shCn4S4cSaA20vwW9VzWiypDNjvr7xmqRChLvVQoSk_Kwm0dBNo273RfdfrbQyzntqWVywcAtbBC5hmSE1UZpr2O7dGsG5XXnP5Jns7bjjqq4U5fpA";

    given().contentType("application/json")
        .body("{\"value\": 5,\n" + "    \"comment\": \"Another comment ...\"\n"
            + "}")
        .cookie("token", token)
        .post("http://localhost:8889/movies/{id}/rate", "1").then()
        .body("result", equalTo("error"));

  }


  @AfterAll
  public static void afterAll() {
    webApp.tearDown();
  }

}
