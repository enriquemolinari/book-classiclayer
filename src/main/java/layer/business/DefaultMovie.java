package layer.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.Movie;
import layer.business.api.MovieCast;
import layer.data.api.MovieCastData;

class DefaultMovie {

  private Long id;
  private String name;
  private String duration;
  private String plot;
  private String coverImage;
  private List<MovieCastData> cast = new ArrayList<>();

  public DefaultMovie(String name, int duration, String plot,
      String coverImage) {
    this.name = name;
    this.duration = new MovieDurationFormat(duration).val();
    this.plot = plot;
    this.coverImage = coverImage;
  }

  public DefaultMovie(Long id, String name, int duration, String plot,
      String coverImage) {
    this(name, duration, plot, coverImage);
    this.id = id;
  }

  public DefaultMovie(Long id, String name, int duration, String plot,
      String coverImage, List<MovieCastData> cast) {
    this(id, name, duration, plot, coverImage);
    this.id = id;
    this.cast = cast;
  }

  public Movie toRecord() {
    var mCasts = this.cast.stream()
        .map(c -> new MovieCast(c.name(), c.surname(), c.characterName()))
        .collect(Collectors.toUnmodifiableList());

    return new Movie(this.id, this.name, this.duration, this.plot,
        this.coverImage, mCasts);
  }
}

