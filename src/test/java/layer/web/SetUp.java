package layer.web;

import org.jdbi.v3.core.Jdbi;
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

public class SetUp {

  private Web web;

  public Web testEnvSetUp() {
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

    this.web = new Web("client-url", 8889, movies, cinema, users);
    web.start();
    return this.web;
  }

  public void tearDown() {
    this.web.close();
  }
}
