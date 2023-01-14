package layer.business;

import java.time.LocalDateTime;
import layer.business.api.ShowRecord;
import layer.data.api.ShowData;

public class Show {
  private Long id;
  private LocalDateTime startTime;
  private Movie movie;
  private Theatre theatre;

  public Show(Long id, LocalDateTime startTime, int duration, String movieName,
      String idCoverImage, String theatreName) {
    this.id = id;
    this.startTime = startTime;
    this.movie = new Movie(movieName, duration, idCoverImage);
    this.theatre = new Theatre(theatreName);
  }

  public Show(ShowData showdata) {
    this(showdata.idShow(), showdata.startTime(), showdata.movieDuration(),
        showdata.movieName(), showdata.idMovieCoverImage(),
        showdata.theatreName());
  }

  public ShowRecord toRecord() {
    return new ShowRecord(this.id, this.movie.name(), this.startDayTime(),
        this.finishAtTime(), this.movie.imgCover(), this.theatre.name(),
        theatre.seats());
  }

  private String startDayTime() {
    return new DayTimeFormatted(startTime).toString();
  }

  private String finishAtTime() {
    return new TimeFormatted(this.startTime.plusMinutes(movie.duration()))
        .toString();
  }
}
