package layer.web;

import static io.restassured.RestAssured.get;
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
    get("http://localhost:8889/shows/1").then().rootPath("show")
        .body("movieName", equalTo("Small Fish")).and()
        .body("duration", equalTo(125)).and().body("price", equalTo(21F)).and()
        .body("seats",
            hasItems(Map.of("id", 1, "seatNumber", 1, "available", true),
                Map.of("id", 2, "seatNumber", 2, "available", true),
                Map.of("id", 70, "seatNumber", 70, "available", true)));
  }

  @AfterAll
  public static void afterAll() {
    webApp.tearDown();
  }


}
