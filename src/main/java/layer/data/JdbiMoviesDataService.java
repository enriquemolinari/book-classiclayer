package layer.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
          "select id_movie, name, duration, plot, id_cover_image, release_date, age_restriction from movie")
          .mapToMap().list();

      var movieIds =
          movies.stream().map(p -> Long.valueOf(p.get("id_movie").toString()))
              .collect(Collectors.toUnmodifiableSet());

      var genres = handle.createQuery(
          "select id_movie, GROUP_CONCAT(description separator ',') as desc "
              + "from movie_genre mg, genre g "
              + "where mg.id_genre = g.id_genre "
              + "and id_movie in (<idmovies>) " + " group by id_movie")
          .bindList("idmovies", movieIds).mapToMap().list();

      var gens = genres.stream()
          .map(g -> Map.of(Long.valueOf(g.get("id_movie").toString()),
              Arrays.asList(g.get("desc").toString().split(","))))
          .collect(Collectors.toUnmodifiableList());

      return movies.stream().map(m -> {

        var idm = Long.valueOf(m.get("id_movie").toString());

        var gensForM = gens.stream().filter(g -> g.containsKey(idm))
            .collect(Collectors.toUnmodifiableList());

        return new ShortMovieData(Long.valueOf(m.get("id_movie").toString()),
            m.get("name").toString(), m.get("plot").toString(),
            Integer.valueOf(m.get("duration").toString()),
            gensForM.get(0).get(idm), m.get("id_cover_image").toString(),
            new ToLocalDate(m.get("release_date")).val().toLocalDate(),
            Integer.valueOf(m.get("age_restriction").toString()));

      }).collect(Collectors.toUnmodifiableList());
    });
  }

  @Override
  public FullMovieData movieDetail(Long idMovie) {
    return jdbi.withHandle(handle -> {
      var movie = handle.createQuery(
          "select id_movie, name, duration, plot, id_cover_image, release_date, age_restriction"
              + " from movie where id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().one();

      var genres = handle
          .createQuery("select g.description from movie_genre mg, genre g "
              + "where g.id_genre = mg.id_genre and mg.id_movie = :idmovie")
          .bind("idmovie", idMovie).mapTo(String.class).list();

      var movieCast = handle.createQuery(
          "select p.name, p.surname, character_name, cast_type from movie_cast mc, person p "
              + "where mc.id_person = p.id_person and id_movie = :idmovie")
          .bind("idmovie", idMovie).mapToMap().list();

      var castList = movieCast.stream()
          .map(m -> new MovieCastData(m.get("name").toString(),
              m.get("surname").toString(),
              m.get("character_name") == null ? ""
                  : m.get("character_name").toString(),
              m.get("cast_type").toString()))
          .collect(Collectors.toList());

      return new FullMovieData(
          new ShortMovieData(Long.valueOf(movie.get("id_movie").toString()),
              movie.get("name").toString(), movie.get("plot").toString(),
              Integer.valueOf(movie.get("duration").toString()), genres,
              movie.get("id_cover_image").toString(),
              new ToLocalDate(movie.get("release_date")).val().toLocalDate(),
              Integer.valueOf(movie.get("age_restriction").toString())),
          castList);
    });
  }
}
