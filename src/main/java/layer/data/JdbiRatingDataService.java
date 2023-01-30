package layer.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.DataException;
import layer.data.api.RatingData;
import layer.data.api.RatingDataService;
import layer.data.api.RatingDetail;

public class JdbiRatingDataService implements RatingDataService {

  private Jdbi jdbi;

  public JdbiRatingDataService(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public void rate(Long idUser, Long idMovie, int value, String comment) {
    checkUserHasRated(idUser, idMovie);

    jdbi.useTransaction(handle -> {
      // there should be a lock here to make this work properly
      // I will leave this without implemented it properly as it is not the purpose of this code
      // reader: remember that locking using 'for update' does not work when use agregation functions
      var actualValues = handle
          .createQuery(
              "SELECT SUM(value) as total_sum, count(value) as total_count "
                  + "from rating_detail where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().one();

      handle.createUpdate(
          "INSERT INTO rating_detail(id_movie, id_user, value, comment, created_at) "
              + "values(:idmovie, :iduser, :value, :comment, :date)")
          .bind("idmovie", idMovie).bind("iduser", idUser).bind("value", value)
          .bind("comment", comment).bind("date", LocalDateTime.now()).execute();

      var existARate = handle
          .createQuery(
              "SELECT 1 as exist_rate from rating where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Integer.class).findOne();

      existARate.ifPresentOrElse((p) -> {
        var newValue = ((new BigDecimal((Long) actualValues.get("total_sum")))
            .add(new BigDecimal(value))).floatValue()
            / ((Long) actualValues.get("total_count") + 1);

        handle
            .createUpdate(
                "UPDATE rating set value = :newvalue where id_movie = :idmovie")
            .bind("idmovie", idMovie).bind("newvalue", newValue).execute();
      }, () -> {
        handle.createUpdate(
            "INSERT INTO rating (id_movie, value) values(:idmovie, :initialValue)")
            .bind("idmovie", idMovie).bind("initialValue", value).execute();
      });
    });
  }

  @Override
  public RatingData rate(Long idMovie) {
    return jdbi.withHandle(handle -> {
      var ratingValue = handle
          .createQuery("SELECT value from rating where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Float.class).findOne();

      var ratingDetail = handle.createQuery(
          "select username, comment, value, created_at from rating_detail rd, users u where rd.id_user = u.id_user and id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().list();

      if (ratingValue.isEmpty()) {
        return new RatingData(0L, 0F, List.of());
      }

      var details = ratingDetail.stream().map(rd -> {
        return new RatingDetail(rd.get("username").toString(),
            new ToLocalDate(rd.get("created_at")).val(),
            Integer.valueOf(rd.get("value").toString()),
            rd.get("comment").toString());

      }).collect(Collectors.toUnmodifiableList());

      return new RatingData(Long.valueOf(ratingDetail.size()),
          ratingValue.get(), details);
    });
  }

  @Override
  public void checkUserHasRated(Long idUser, Long idMovie) {
    jdbi.useHandle(handle -> {
      var hasAlreadyVote = handle.createQuery(
          "SELECT 1 from rating_detail where id_movie = :idmovie and id_user = :iduser")
          .bind("idmovie", idMovie).bind("iduser", idUser).mapTo(Integer.class)
          .findFirst();

      if (hasAlreadyVote.isPresent()) {
        throw new DataException("You have already voted");
      }
    });
  }

}
