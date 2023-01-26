package layer.main;

import org.jdbi.v3.core.Jdbi;
import layer.business.DefaultCinemaShows;
import layer.business.DefaultMovies;
import layer.business.DefaultUsers;
import layer.business.MailTrapEmailService;
import layer.business.PasetoToken;
import layer.business.SomePaymentProvider;
import layer.data.JdbiMoviesDataService;
import layer.data.JdbiRatingDataService;
import layer.data.JdbiShowsDataService;
import layer.data.JdbiUserAuthDataService;
import layer.web.Web;

public class Main {

  public static void main(String[] args) {
    // memory
    // var connStr = jdbc:hsqldb:mem;create=true

    // client-server
    var connStr = "jdbc:hsqldb:hsql://localhost/xdb";

    new SetUpDatabase(connStr).start();

    String mtuser = System.getProperty("mailt-user");
    String mtpwd = System.getProperty("mailt-pwd");
    String secret = System.getProperty("token-secret");

    if (mtuser == null || mtpwd == null || secret == null) {
      throw new IllegalArgumentException(
          "mailt-user, mailt-pwd and secret values must be passed as a jvm argument");
    }

    var jdbi = Jdbi.create(connStr);
    var movies = new DefaultMovies(new JdbiMoviesDataService(jdbi),
        new JdbiRatingDataService(jdbi));
    var cinema = new DefaultCinemaShows(new JdbiShowsDataService(jdbi),
        new JdbiUserAuthDataService(jdbi),
        new MailTrapEmailService(mtuser, mtpwd, "info@cinema.com"),
        new SomePaymentProvider());
    var users = new DefaultUsers(new JdbiUserAuthDataService(jdbi),
        new PasetoToken(secret));

    new Web("http://localhost:3000", 8888, movies, cinema, users).start();
  }
}
