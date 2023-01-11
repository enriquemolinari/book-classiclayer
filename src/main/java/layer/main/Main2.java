package layer.main;

import java.math.BigDecimal;
import org.jdbi.v3.core.Jdbi;
import layer.data.jdbi.JdbiRatingData;

public class Main2 {

  public static void main(String[] args) {
    // actual rating
    var rd =
        new JdbiRatingData(Jdbi.create("jdbc:hsqldb:hsql://localhost/xdb"));
    // var rv = rd.rate(2L);
    // System.out.println(rv.numberOfVotes());
    // System.out.println(rv.value());

    // voting
    rd.rate(3L, 1L, new BigDecimal(4));

  }
}
