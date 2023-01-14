package layer.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.MovieCastRecord;
import layer.business.api.MovieRecord;
import layer.data.api.MovieCastData;

class Movie {

  private Long id;
  private String name;
  private String formattedDuration;
  private int duration;
  private String plot;
  private String coverImage;
  private List<MovieCastData> cast = new ArrayList<>();

  public Movie(String name, int duration, String coverImage) {
    this.name = name;
    this.formattedDuration = new MovieDurationFormat(duration).val();
    this.duration = duration;
    this.coverImage = coverImage;
  }

  public Movie(String name, int duration, String plot, String coverImage) {
    this(name, duration, coverImage);
    this.plot = plot;
  }

  public Movie(Long id, String name, int duration, String plot,
      String coverImage) {
    this(name, duration, plot, coverImage);
    this.id = id;
  }

  public Movie(Long id, String name, int duration, String plot,
      String coverImage, List<MovieCastData> cast) {
    this(id, name, duration, plot, coverImage);
    this.id = id;
    this.cast = cast;
  }

  public MovieRecord toRecord() {
    var mCasts = this.cast.stream()
        .map(c -> new MovieCastRecord(c.name(), c.surname(), c.characterName()))
        .collect(Collectors.toUnmodifiableList());

    return new MovieRecord(this.id, this.name, this.formattedDuration,
        this.plot, this.coverImage, mCasts);
  }

  int duration() {
    return this.duration;
  }

  String name() {
    return this.name;
  }

  String imgCover() {
    return this.coverImage;
  }
}

