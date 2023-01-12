package layer.web;

import java.util.Map;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import layer.business.api.CinemaException;
import layer.business.api.Movies;

public class Web {

  private int webPort;
  private Movies movies;

  public Web(int webPort, Movies moviesService) {
    this.webPort = webPort;
    this.movies = moviesService;
  }

  public void start() {
    Javalin app = Javalin.create().start(this.webPort);
    app.get("/movies", allMovies());
    app.get("/movie", movieDetail());

    app.exception(CinemaException.class, (e, ctx) -> {
      ctx.json(Map.of("result", "error", "message", e.getMessage()));
      // log error in a stream...
    });

    app.exception(Exception.class, (e, ctx) -> {
      ctx.json(
          Map.of("result", "error", "message", "Ups, somethong went wrong"));
      // log error in a stream...
    });
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
