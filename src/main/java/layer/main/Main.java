package layer.main;

public class Main {

  public static void main(String[] args) {

    // memory
    // new SetUpDatabase("jdbc:hsqldb:mem;create=true").start();
    // disk
    new SetUpDatabase("jdbc:hsqldb:file:/home/enrique/mycinema").start();

    // user
    // Optional<UserData> user =
    // new JdbiUserAuth(Jdbi.create("jdbc:hsqldb:file:/home/enrique/mycinema"))
    // .login("emolinari", "123");
    //
    // user.ifPresent((u) -> System.out.println(u.id()));
    // user.ifPresent((u) -> System.out.println(u.userName()));
    // user.ifPresent((u) -> System.out.println(u.points()));

    // actual rating
    // var rd = new JdbiRatingData(
    // Jdbi.create("jdbc:hsqldb:file:/home/enrique/mycinema"));
    // var rv = rd.rate(2L);
    // System.out.println(rv.numberOfVotes());
    // System.out.println(rv.value());

    // voting
    // rd.rate(3L, 2L, new BigDecimal(3.2));

  }
}
