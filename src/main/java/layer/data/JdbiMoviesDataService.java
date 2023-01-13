package layer.data;

import java.util.List;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.FullMovieData;
import layer.data.api.MovieCastData;
import layer.data.api.MoviesDataService;
import layer.data.api.ShortMovieData;

public class JdbiMoviesDataService implements MoviesDataService {

  private Jdbi jdbi;

  public JdbiMoviesDataService(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<ShortMovieData> allMovies() {
    return jdbi.withHandle(handle -> {
      var movies = handle.createQuery(
          "select id_movie, name, duration, plot, id_cover_image from movie")
          .mapToMap().list();

      return movies.stream()
          .map(m -> new ShortMovieData(
              Long.valueOf(m.get("id_movie").toString()),
              m.get("name").toString(), m.get("plot").toString(),
              Integer.valueOf(m.get("duration").toString()),
              m.get("id_cover_image").toString()))
          .collect(Collectors.toUnmodifiableList());
    });
  }

  @Override
  public FullMovieData movieDetail(Long idMovie) {
    return jdbi.withHandle(handle -> {
      var movie = handle.createQuery(
          "select id_movie, name, duration, plot, id_cover_image from movie where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().one();

      var movieCast = handle.createQuery(
          "select p.name, p.surname, character_name from movie_cast mc, person p "
              + "where mc.id_person = p.id_person and id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().list();

      var castList = movieCast.stream()
          .map(m -> new MovieCastData(m.get("name").toString(),
              m.get("surname").toString(), m.get("character_name").toString()))
          .collect(Collectors.toList());

      return new FullMovieData(
          new ShortMovieData(Long.valueOf(movie.get("id_movie").toString()),
              movie.get("name").toString(), movie.get("plot").toString(),
              Integer.valueOf(movie.get("duration").toString()),
              movie.get("id_cover_image").toString()),
          castList);
    });
  }
}
