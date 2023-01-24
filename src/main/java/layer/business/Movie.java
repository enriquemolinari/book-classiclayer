package layer.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.MovieCastRecord;
import layer.business.api.MovieRecord;
import layer.business.api.MovieShows;
import layer.data.api.MovieCastData;

class Movie {

  private Long id;
  private String name;
  private String formattedDuration;
  private int duration;
  private String plot;
  private List<MovieCastData> cast = new ArrayList<>();
  private List<Show> shows = new ArrayList<>();
  private String coverImg;
  private List<String> genres = new ArrayList<>();

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
      List<String> genres, List<MovieCastData> cast) {
    this(id, name, duration, plot, coverImg, genres);
    this.id = id;
    this.cast = cast;
  }

  public MovieRecord toRecord() {
    var mCasts = this.cast.stream()
        .map(c -> new MovieCastRecord(c.name(), c.surname(), c.characterName()))
        .collect(Collectors.toUnmodifiableList());

    return new MovieRecord(this.id, this.name, this.formattedDuration,
        this.plot, this.coverImg, this.genres, mCasts);
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

