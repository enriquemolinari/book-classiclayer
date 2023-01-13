package layer.data;

import java.math.BigDecimal;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.DataException;
import layer.data.api.RatingDataService;
import layer.data.api.RatingData;

public class JdbiRatingDataService implements RatingDataService {

  private Jdbi jdbi;

  public JdbiRatingDataService(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public void rate(Long idUser, Long idMovie, BigDecimal value) {
    checkUserHasVoted(idUser, idMovie);

    jdbi.useTransaction(handle -> {
      // there should be a lock here to make this work properly
      // I will leave this without implemented it properly as it is not the purpose of this code
      // reader: remember that locking using 'for update' does not work when use agregation functions
      var actualValues = handle.createQuery(
          "SELECT SUM(value) as total_sum, count(value) as total_count from rating_detail where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().one();

      handle.createUpdate(
          "INSERT INTO rating_detail(id_movie, id_user, value) values(:idmovie, :iduser, :value)")
          .bind("idmovie", idMovie).bind("iduser", idUser).bind("value", value)
          .execute();

      var existARate = handle
          .createQuery(
              "SELECT 1 as exist_rate from rating where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Integer.class).findOne();

      existARate.ifPresentOrElse((p) -> {
        var newValue = (((BigDecimal) actualValues.get("total_sum")).add(value))
            .floatValue() / ((Long) actualValues.get("total_count") + 1);

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

      var numberOfVotes = handle.createQuery(
          "SELECT count(*) as votes from rating_detail where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Long.class).one();

      if (ratingValue.isEmpty()) {
        return new RatingData(numberOfVotes, 0F);
      }

      return new RatingData(numberOfVotes, ratingValue.get());
    });
  }

  private void checkUserHasVoted(Long idUser, Long idMovie) {
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