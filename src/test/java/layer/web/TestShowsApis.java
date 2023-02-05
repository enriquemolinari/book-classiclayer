package layer.web;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestShowsApis {
  private static SetUp webApp;

  @BeforeAll
  public static void beforeAll() {
    webApp = new SetUp();
    webApp.testEnvSetUp();
  }

  @Test
  public void testShows() {
    get("http://localhost:8889/shows").then().rootPath("showsThisWeek")
        .body("movie.name",
            hasItems("Rock in the School", "Small Fish", "Crash Tea",
                "Running far away"))
        .and()
        .body("movie.duration",
            hasItems("1hr 49mins", "2hrs 05mins", "1hr 45mins", "1hr 45mins"))
        .and().body("shows.price", hasItems(List.of(19.0F, 19.0F),
            List.of(21.0F, 21.0F), List.of(19.0F), List.of(19.0F)));
  }

  @Test
  public void testShowDetail() {
    get("http://localhost:8889/shows/2").then().rootPath("show")
        .body("movieName", equalTo("Small Fish")).and()
        .body("duration", equalTo(125)).and().body("price", equalTo(21F)).and()
        .body("seats",
            hasItems(Map.of("id", 1, "seatNumber", 1, "available", true),
                Map.of("id", 2, "seatNumber", 2, "available", true),
                Map.of("id", 70, "seatNumber", 70, "available", true)));
  }

  @Test
  public void testReserveOk() {
    String token = given().contentType("application/json")
        .body("{\"username\": \"jsimini\", \"password\": \"123\"}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json").body("{\"seats\": [1,2,3]}")
        .cookie("token", token).post("http://localhost:8889/shows/1/reserve")
        .then().body("result", equalTo("success"));

    get("http://localhost:8889/shows/1").then().rootPath("show")
        .body("movieName", equalTo("Small Fish")).and()
        .body("duration", equalTo(125)).and().body("price", equalTo(21F)).and()
        .body("seats",
            hasItems(Map.of("id", 1, "seatNumber", 1, "available", false),
                Map.of("id", 2, "seatNumber", 2, "available", false),
                Map.of("id", 3, "seatNumber", 3, "available", false),
                Map.of("id", 4, "seatNumber", 4, "available", true),
                Map.of("id", 5, "seatNumber", 5, "available", true),
                Map.of("id", 70, "seatNumber", 70, "available", true)));
  }

  @Test
  public void testReserveAlreadyReserved() {
    String token = given().contentType("application/json")
        .body("{\"username\": \"jsimini\", \"password\": \"123\"}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json").body("{\"seats\": [4, 5]}")
        .cookie("token", token).post("http://localhost:8889/shows/1/reserve")
        .then().body("result", equalTo("success"));

    given().contentType("application/json").body("{\"seats\": [4, 5]}")
        .cookie("token", token).post("http://localhost:8889/shows/1/reserve")
        .then().body("result", equalTo("error"));
  }

  @Test
  public void testConfirmWithoutReserve() {
    String token = given().contentType("application/json")
        .body("{\"username\": \"jsimini\", \"password\": \"123\"}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json").body(
        "{\"seats\": [72, 73], \"number\": \"123456\", \"code\" : \"123\", \"name\" : \"Enrique Molinari\"}")
        .cookie("token", token).post("http://localhost:8889/shows/4/pay").then()
        .body("result", equalTo("error"));
  }

  @Test
  public void testConfirmWithoutReserveOneSeat() {
    String token = given().contentType("application/json")
        .body("{\"username\": \"jsimini\", \"password\": \"123\"}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json").body("{\"seats\": [84, 85]}")
        .cookie("token", token).post("http://localhost:8889/shows/4/reserve")
        .then().body("result", equalTo("success"));

    given().contentType("application/json").body(
        "{\"seats\": [84, 85, 86], \"number\": \"123456\", \"code\" : \"123\", \"name\" : \"Enrique Molinari\"}")
        .cookie("token", token).post("http://localhost:8889/shows/4/pay").then()
        .body("result", equalTo("error"));
  }

  @Test
  public void testConfirmOk() {
    String token = given().contentType("application/json")
        .body("{\"username\": \"jsimini\", \"password\": \"123\"}")
        .post("http://localhost:8889/login").body().jsonPath()
        .getString("user.token");

    given().contentType("application/json").body("{\"seats\": [74, 75]}")
        .cookie("token", token).post("http://localhost:8889/shows/3/reserve")
        .then().body("result", equalTo("success"));

    given().contentType("application/json").body(
        "{\"seats\": [74, 75], \"number\": \"123456\", \"code\" : \"123\", \"name\" : \"Enrique Molinari\"}")
        .cookie("token", token).post("http://localhost:8889/shows/3/pay").then()
        .body("result", equalTo("success"));
  }

  @AfterAll
  public static void afterAll() {
    webApp.tearDown();
  }
}
