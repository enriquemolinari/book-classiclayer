package layer.data.jdbi;

import org.jdbi.v3.core.Jdbi;
import layer.data.api.DataException;
import layer.data.api.RatingData;
import layer.data.api.RatingRecord;

public class JdbiRatingData implements RatingData {

  private Jdbi jdbi;

  public JdbiRatingData(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public void rate(Long idUser, Long idMovie, int value) {
    checkUserHasVoted(idUser, idMovie);

    jdbi.useTransaction(handle -> {

      // this query should lock the rows
      var actualRating = handle.createQuery(
          "SELECT SUM(value)/count(value) as actual_rating from rating_detail where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Float.class).findOne();

      handle.createUpdate(
          "INSERT INTO rate_detail(id_movie, id_user, value) values(:idmovie, :iduser, :value)")
          .bind("idmovie", idMovie).bind("iduser", idUser).bind("value", value)
          .execute();

      // TODO: continue...

    });

  }

  @Override
  public RatingRecord rate(Long idMovie) {
    return jdbi.withHandle(handle -> {
      var ratingValue = handle
          .createQuery("SELECT value from rating where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Float.class).findOne();

      var numberOfVotes = handle.createQuery(
          "SELECT count(*) as votes from rating_detail where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(Long.class).one();

      if (ratingValue.isEmpty()) {
        return new RatingRecord(numberOfVotes, 0F);
      }

      return new RatingRecord(numberOfVotes, ratingValue.get());
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
