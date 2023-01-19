package layer.main;

import org.jdbi.v3.core.Jdbi;
import layer.business.DefaultCinemaShows;
import layer.business.DefaultMovies;
import layer.business.MailTrapEmailService;
import layer.business.SomePaymentProvider;
import layer.data.JdbiMoviesDataService;
import layer.data.JdbiShowsDataService;
import layer.data.JdbiUserAuthDataService;
import layer.web.Web;

public class Main {

  public static void main(String[] args) {
    // memory
    // var connStr = jdbc:hsqldb:mem;create=true

    // client-server
    var connStr = "jdbc:hsqldb:hsql://localhost/xdb";

    // new SetUpDatabase(connStr).start();

    String mtuser = System.getProperty("mailt-user");
    String mtpwd = System.getProperty("mailt-pwd");

    if (mtuser == null || mtpwd == null) {
      throw new IllegalArgumentException(
          "mailt-user and mailt-pwd values must be passed as a jvm argument");
    }

    var jdbi = Jdbi.create(connStr);
    var movies = new DefaultMovies(new JdbiMoviesDataService(jdbi));
    var cinema = new DefaultCinemaShows(new JdbiShowsDataService(jdbi),
        new JdbiUserAuthDataService(jdbi),
        new MailTrapEmailService(mtuser, mtpwd, "info@cinema.com"),
        new SomePaymentProvider());

    new Web(8888, movies, cinema).start();
  }
}
