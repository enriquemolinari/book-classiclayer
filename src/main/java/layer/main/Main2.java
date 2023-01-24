package layer.main;

import org.jdbi.v3.core.Jdbi;
import layer.business.DefaultCinemaShows;
import layer.business.MailTrapEmailService;
import layer.business.SomePaymentProvider;
import layer.data.JdbiShowsDataService;
import layer.data.JdbiUserAuthDataService;

public class Main2 {

  public static void main(String[] args) {
    var connStr = "jdbc:hsqldb:hsql://localhost/xdb";
    var jdbi = Jdbi.create(connStr);

    var cinema = new DefaultCinemaShows(new JdbiShowsDataService(jdbi),
        new JdbiUserAuthDataService(jdbi),
        new MailTrapEmailService(null, null, "info@cinema.com"),
        new SomePaymentProvider());

    System.out.println(cinema.playingThisWeek());

  }

}
