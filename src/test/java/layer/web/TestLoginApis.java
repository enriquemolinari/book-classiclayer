package layer.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestLoginApis {

  private static SetUp webApp;

  @BeforeAll
  public static void beforeAll() {
    webApp = new SetUp();
    webApp.testEnvSetUp();
  }

  @Test
  public void testLoginOk() {
    given().contentType("application/json")
        .body("{\"username\": \"jsimini\",\n" + "    \"password\": \"123\"\n"
            + "}")
        .post("http://localhost:8889/login").then().body("user", hasKey("id"))
        .body("user", hasKey("username")).body("user", hasKey("token"))
        .body("result", equalTo("success")).body("user.points", equalTo(0));
  }

  @AfterAll
  public static void afterAll() {
    webApp.tearDown();
  }

}
