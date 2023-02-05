package layer.web;

import java.util.Map;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.staticfiles.Location;
import layer.business.api.CinemaShows;
import layer.business.api.Movies;
import layer.business.api.UnauthorizedException;
import layer.business.api.Users;

public class Web {

  private int webPort;
  private Movies movies;
  private CinemaShows shows;
  private Users users;
  private String corsAllowHost;
  private Javalin app;

  public Web(String corsAllowHost, int webPort, Movies moviesService,
      CinemaShows shows, Users users) {
    this.webPort = webPort;
    this.movies = moviesService;
    this.shows = shows;
    this.users = users;
    this.corsAllowHost = corsAllowHost;
  }

  public void start() {
    this.app = Javalin.create(config -> {
      config.staticFiles.add(s -> {
        s.hostedPath = "/assets";
        s.directory = "/public";
        s.location = Location.CLASSPATH;
      });

      config.plugins.enableCors(cors -> {
        cors.add(it -> {
          it.allowHost(this.corsAllowHost);
          it.allowCredentials = true;
        });
      });
    });
    app.post("/login", login());
    app.post("/logout", logout());
    app.get("/movies", allMovies());
    app.get("/movies/{id}", movieDetail());
    app.post("/movies/{id}/rate", rateMovie());
    app.get("/movies/{id}/rate", retrieveRate());
    app.get("/shows", playing());
    app.get("/shows/{id}", showDetail());
    app.post("/shows/{id}/reserve", reserve());
    app.post("/shows/{id}/pay", confirmReservation());

    app.exception(UnauthorizedException.class, (e, ctx) -> {
      ctx.status(401);
      ctx.json(Map.of("result", "error", "message", e.getMessage()));
      // log error in a stream...
    });

    app.exception(RuntimeException.class, (e, ctx) -> {
      ctx.json(Map.of("result", "error", "message", e.getMessage()));
      // log error in a stream...
    });

    app.exception(Exception.class, (e, ctx) -> {
      ctx.json(
          Map.of("result", "error", "message", "Ups, something went wrong"));
      // log error in a stream...
    }).start(this.webPort);
  }

  void close() {
    this.app.close();
  }

  private Long checkLoggedIn(Context ctx) {
    var tokenValue = ctx.cookie("token");
    return this.users.userIdFrom(tokenValue);
  }

  private Handler retrieveRate() {
    return ctx -> {

      var r = this.movies.rating(ctx.pathParamAsClass("id", Long.class).get());

      ctx.json(Map.of("result", "success", "rating", r));
    };
  }

  private Handler rateMovie() {
    return ctx -> {
      var uid = checkLoggedIn(ctx);
      var r = ctx.bodyAsClass(RateMovieRequest.class);

      this.movies.rateMovie(uid, ctx.pathParamAsClass("id", Long.class).get(),
          r.value(), r.comment());

      ctx.json(Map.of("result", "success"));
    };
  }

  private Handler login() {
    return ctx -> {
      var r = ctx.bodyAsClass(LoginRequest.class);

      var user = this.users.login(r.username(), r.password());

      ctx.res().setHeader("Set-Cookie",
          "token=" + user.token() + ";path=/; HttpOnly; ");

      ctx.json(Map.of("result", "success", "user", user));
    };
  }

  private Handler logout() {
    return ctx -> {
      // want register login/logout time?
      // just remove the token cookie
      ctx.removeCookie("token");
      ctx.json(Map.of("result", "success"));
    };
  }

  private Handler confirmReservation() {
    return ctx -> {
      var r = ctx.bodyAsClass(PaymentRequest.class);
      var showId = ctx.pathParamAsClass("id", Long.class).get();
      var uid = checkLoggedIn(ctx);

      var ticket =
          this.shows.pay(r.toCreditCardRecord(), showId, uid, r.seats());
      ctx.json(Map.of("result", "success", "ticket", ticket));
    };
  }

  private Handler reserve() {
    return ctx -> {
      var request = ctx.bodyAsClass(ReservationRequest.class);
      var showId = ctx.pathParamAsClass("id", Long.class).get();
      var uid = checkLoggedIn(ctx);

      this.shows.makeReservation(showId, uid, request.seats());
      ctx.json(Map.of("result", "success"));
    };
  }

  private Handler playing() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "showsThisWeek",
          this.shows.playingThisWeek()));
    };
  }

  private Handler showDetail() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "show",
          this.shows.show(ctx.pathParamAsClass("id", Long.class).get())));
    };
  }

  private Handler allMovies() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "movies", this.movies.movies()));
    };
  }

  public Handler movieDetail() {
    return ctx -> {
      ctx.json(Map.of("result", "success", "movie",
          this.movies.detail(ctx.pathParamAsClass("id", Long.class).get())));
    };
  }
}
