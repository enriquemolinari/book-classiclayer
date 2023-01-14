package layer.web;

import java.util.Map;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import layer.business.api.CinemaException;
import layer.business.api.CinemaShows;
import layer.business.api.Movies;

public class Web {

  private int webPort;
  private Movies movies;
  private CinemaShows shows;

  public Web(int webPort, Movies moviesService, CinemaShows shows) {
    this.webPort = webPort;
    this.movies = moviesService;
    this.shows = shows;
  }

  public void start() {
    Javalin app = Javalin.create().start(this.webPort);
    app.get("/movies", allMovies());
    app.get("/movie", movieDetail());
    app.get("/playing", playing());

    app.exception(CinemaException.class, (e, ctx) -> {
      ctx.json(Map.of("result", "error", "message", e.getMessage()));
      // log error in a stream...
      // for now just on console...
      e.printStackTrace();
    });

    app.exception(Exception.class, (e, ctx) -> {
      ctx.json(
          Map.of("result", "error", "message", "Ups, somethong went wrong"));
      // log error in a stream...
      // for now just on console...
      e.printStackTrace();
    });
  }

  private Handler playing() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "showsThisWeek",
          this.shows.playingThisWeek()));
    };
  }

  private Handler allMovies() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "movies", this.movies.movies()));
    };
  }

  private Handler movieDetail() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "movie", this.movies
          .detail(ctx.queryParamAsClass("idmovie", Long.class).get())));
    };
  }
}
