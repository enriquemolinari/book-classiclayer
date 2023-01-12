package layer.business;

import java.util.stream.Collectors;
import layer.business.api.Movie;
import layer.business.api.Movies;
import layer.data.api.MoviesData;

public class DefaultMovies implements Movies {

  private MoviesData movieData;

  public DefaultMovies(MoviesData movieData) {
    this.movieData = movieData;
  }

  @Override
  public Iterable<Movie> movies() {
    var movies = this.movieData.allMovies();

    return movies.stream()
        .map(m -> new DefaultMovie(m.idMovie(), m.name(), m.duration(),
            m.plot(), m.idCoverImage()).toMovieRecord())
        .collect(Collectors.toList());
  }

  @Override
  public Movie detail(Long id) {
    var m = this.movieData.movieDetail(id);

    return new DefaultMovie(m.shortMovie().idMovie(), m.shortMovie().name(),
        m.shortMovie().duration(), m.shortMovie().plot(),
        m.shortMovie().idCoverImage(), m.casts()).toMovieRecord();
  }
}
