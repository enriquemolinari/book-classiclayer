package layer.data;

import java.util.List;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.FullMovieRecord;
import layer.data.api.MovieCastRecord;
import layer.data.api.MoviesData;
import layer.data.api.ShortMovieRecord;

public class JdbiMoviesData implements MoviesData {

  private Jdbi jdbi;

  public JdbiMoviesData(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<ShortMovieRecord> allMovies() {
    return jdbi.withHandle(handle -> {
      var movies = handle.createQuery(
          "select id_movie, name, duration, plot, id_cover_image from movie")
          .mapToMap().list();

      return movies.stream()
          .map(m -> new ShortMovieRecord(
              Long.valueOf(m.get("id_movie").toString()),
              m.get("name").toString(), m.get("plot").toString(),
              Integer.valueOf(m.get("duration").toString()),
              m.get("id_cover_image").toString()))
          .collect(Collectors.toList());
    });
  }

  @Override
  public FullMovieRecord movieDetail(Long idMovie) {
    return jdbi.withHandle(handle -> {
      var movie = handle.createQuery(
          "select id_movie, name, duration, plot, id_cover_image from movie where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().one();

      var movieCast = handle.createQuery(
          "select p.name, p.surname, character_name from movie_cast mc, person p "
              + "where mc.id_person = p.id_person and id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().list();

      var castList = movieCast.stream()
          .map(m -> new MovieCastRecord(m.get("name").toString(),
              m.get("surname").toString(), m.get("character_name").toString()))
          .collect(Collectors.toList());

      return new FullMovieRecord(
          new ShortMovieRecord(Long.valueOf(movie.get("id_movie").toString()),
              movie.get("name").toString(), movie.get("plot").toString(),
              Integer.valueOf(movie.get("duration").toString()),
              movie.get("id_cover_image").toString()),
          castList);
    });
  }
}
