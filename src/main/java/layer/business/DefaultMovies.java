package layer.business;

import java.util.stream.Collectors;
import layer.business.api.MovieRecord;
import layer.business.api.Movies;
import layer.data.api.MoviesDataService;

public class DefaultMovies implements Movies {

  private MoviesDataService movieData;

  public DefaultMovies(MoviesDataService movieData) {
    this.movieData = movieData;
  }

  @Override
  public Iterable<MovieRecord> movies() {
    var movies = this.movieData.allMovies();

    return movies.stream()
        .map(m -> new Movie(m.idMovie(), m.name(), m.duration(),
            m.plot(), m.idCoverImage()).toRecord())
        .collect(Collectors.toList());
  }

  @Override
  public MovieRecord detail(Long id) {
    var m = this.movieData.movieDetail(id);

    return new Movie(m.shortMovie().idMovie(), m.shortMovie().name(),
        m.shortMovie().duration(), m.shortMovie().plot(),
        m.shortMovie().idCoverImage(), m.casts()).toRecord();
  }
}
