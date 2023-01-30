package layer.business;

import java.util.stream.Collectors;
import layer.business.api.CinemaException;
import layer.business.api.MovieRecord;
import layer.business.api.Movies;
import layer.business.api.RatingRecord;
import layer.data.api.DataException;
import layer.data.api.MoviesDataService;
import layer.data.api.RatingDataService;
import layer.data.api.UserAuthDataService;

public class DefaultMovies implements Movies {

  private MoviesDataService movieData;
  private RatingDataService ratingData;
  private UserAuthDataService usersData;

  public DefaultMovies(MoviesDataService movieData,
      RatingDataService ratingData, UserAuthDataService usersData) {
    this.movieData = movieData;
    this.ratingData = ratingData;
    this.usersData = usersData;
  }

  @Override
  public Iterable<MovieRecord> movies() {
    var movies = this.movieData.allMovies();

    return movies.stream()
        .map(m -> new Movie(m.idMovie(), m.name(), m.duration(), m.plot(),
            m.idCoverImage(), m.genres()).toRecord())
        .collect(Collectors.toList());
  }

  @Override
  public MovieRecord detail(Long id) {
    var m = this.movieData.movieDetail(id);
    var r = this.ratingData.rate(id);

    return new Movie(m.shortMovie().idMovie(), m.shortMovie().name(),
        m.shortMovie().duration(), m.shortMovie().plot(),
        m.shortMovie().idCoverImage(), m.shortMovie().genres(), m.casts(),
        m.shortMovie().releaseDate(), m.shortMovie().ageRestriction(), r)
            .toRecord();
  }

  @Override
  public RatingRecord rating(Long idMovie) {

    var ratingData = this.ratingData.rate(idMovie);
    var ratings = new Ratings(ratingData.value(), ratingData.totalVotes(),
        ratingData.ratingDetail());
    return ratings.toRecord();
  }

  @Override
  public void rateMovie(Long userId, Long idMovie, int rateValue,
      String comment) {

    try {
      this.ratingData.checkUserHasRated(userId, idMovie);
    } catch (DataException de) {
      throw new CinemaException(de, "You have already voted this movie");
    }

    try {
      var user = this.usersData.details(userId);
      var uRate =
          new UserRate(userId, user.data().userName(), rateValue, comment);

      this.ratingData.rate(userId, idMovie, uRate.voteValue(), uRate.comment());
    } catch (DataException de) {
      throw new CinemaException(de, "rate could not be done");
    }
  }
}
