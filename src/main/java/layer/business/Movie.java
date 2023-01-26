package layer.business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import layer.business.api.MovieCastRecord;
import layer.business.api.MovieRecord;
import layer.business.api.MovieShows;
import layer.data.api.MovieCastData;
import layer.data.api.RatingData;

class Movie {

  private static final String ACTOR_TYPE = "A";
  private static final Object DIRECTOR_TYPE = "D";
  private Long id;
  private String name;
  private String formattedDuration;
  private int duration;
  private String plot;
  private List<MovieCastData> cast = new ArrayList<>();
  private List<Show> shows = new ArrayList<>();
  private String coverImg;
  private List<String> genres = new ArrayList<>();
  private LocalDate releaseDate;
  private int ageRestriction;
  private Ratings ratings;

  public Movie(String name, int duration) {
    this.name = name;
    this.formattedDuration = new MovieDurationFormat(duration).val();
    this.duration = duration;
  }

  public Movie(Long id, String name, int duration) {
    this(name, duration);
    this.id = id;
  }

  public Movie(String name, int duration, String plot) {
    this(name, duration);
    this.plot = plot;
  }

  public Movie(Long id, String name, int duration, String plot, String coverImg,
      List<String> genres) {
    this(name, duration, plot);
    this.id = id;
    this.coverImg = coverImg;
    this.genres = genres;
  }

  public Movie(Long id, String name, int duration, String plot, String coverImg,
      List<String> genres, List<MovieCastData> cast, LocalDate releaseDate,
      int ageRestriction, RatingData ratingData) {
    this(id, name, duration, plot, coverImg, genres);
    this.id = id;
    this.cast = cast;
    this.ageRestriction = ageRestriction;
    this.releaseDate = releaseDate;
    this.ratings = new Ratings(ratingData.value(), ratingData.totalVotes(),
        ratingData.ratingDetail());
  }

  public MovieRecord toRecord() {
    var mCasts = this.cast.stream().filter(c -> c.type().equals(ACTOR_TYPE))
        .map(c -> new MovieCastRecord(c.name(), c.surname(), c.characterName()))
        .collect(Collectors.toUnmodifiableList());

    var directorName =
        this.cast.stream().filter(c -> c.type().equals(DIRECTOR_TYPE))
            .map(c -> c.name() + " " + c.surname()).findFirst().get();

    return new MovieRecord(this.id, this.name, this.formattedDuration,
        this.plot, this.coverImg, this.genres, mCasts,
        this.releaseDate == null ? null : releaseDate(), this.ageRestriction,
        this.ratings == null ? null : this.ratings.toRecord(), releaseYear(),
        directorName);
  }

  int releaseYear() {
    return this.releaseDate.getYear();
  }

  String releaseDate() {
    return this.releaseDate.format(DateTimeFormatter
        .ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.ENGLISH));
  }

  void addShow(Show show) {
    this.shows.add(show);
  }

  int duration() {
    return this.duration;
  }


  String name() {
    return this.name;
  }

  public MovieShows toMovieShow() {
    var showsTimeRecord = this.shows.stream().map(s -> s.toTime())
        .collect(Collectors.toUnmodifiableList());

    return new MovieShows(this.toRecord(), showsTimeRecord);

  }
}

