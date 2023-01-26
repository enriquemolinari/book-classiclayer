package layer.business;

import java.util.stream.Collectors;
import layer.business.api.MovieRecord;
import layer.business.api.Movies;
import layer.data.api.MoviesDataService;
import layer.data.api.RatingDataService;

public class DefaultMovies implements Movies {

  private MoviesDataService movieData;
  private RatingDataService ratingData;

  public DefaultMovies(MoviesDataService movieData,
      RatingDataService ratingData) {
    this.movieData = movieData;
    this.ratingData = ratingData;
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
}
