package layer.main;

import java.util.List;
import org.jdbi.v3.core.Jdbi;
import layer.data.jdbi.JdbiShowsData;

public class Main {

  public static void main(String[] args) {

    // memory
    // new SetUpDatabase("jdbc:hsqldb:mem;create=true").start();
    // disk
    // new SetUpDatabase("jdbc:hsqldb:file:/home/enrique/mycinema").start();

    // client-server
    // new SetUpDatabase("jdbc:hsqldb:hsql://localhost/xdb").start();


    // user
    // Optional<UserData> user =
    // new JdbiUserAuth(Jdbi.create("jdbc:hsqldb:file:/home/enrique/mycinema"))
    // .login("emolinari", "123");
    //
    // user.ifPresent((u) -> System.out.println(u.id()));
    // user.ifPresent((u) -> System.out.println(u.userName()));
    // user.ifPresent((u) -> System.out.println(u.points()));

    // actual rating
    // var rd =
    // new JdbiRatingData(Jdbi.create("jdbc:hsqldb:hsql://localhost/xdb"));
    // var rv = rd.rate(2L);
    // System.out.println(rv.numberOfVotes());
    // System.out.println(rv.value());

    // voting
    // rd.rate(4L, 1L, new BigDecimal(3.2));

    var sd = new JdbiShowsData(Jdbi.create("jdbc:hsqldb:hsql://localhost/xdb"));
    // var res = sd.playingNow(LocalDateTime.now().plusDays(3));
    // for (PlayingRecord pr : res) {
    // System.out.println(pr.movieName());
    // System.out.println(pr.startTime());
    // }

    // var sr = sd.show(3L);
    // System.out.println(sr.movieCover());
    // System.out.println(sr.movieName());
    // System.out.println(sr.movieDuration());
    // System.out.println(sr.idShow());
    // System.out.println(sr.startTime());
    //
    // for (SeatRecord ser : sr.seats()) {
    // System.out.println(ser.number() + " " + ser.idSeat() + " "
    // + ser.reserved() + " " + ser.confirmed());
    // }
    // }

    sd.reserve(1L, 1L, List.of(1L, 2L, 3L));
  }
}
