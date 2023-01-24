package layer.business;

import java.time.LocalDateTime;
import java.util.List;
import layer.business.api.ShowRecord;
import layer.business.api.ShowTimeRecord;
import layer.data.api.PlayingData;
import layer.data.api.SeatData;
import layer.data.api.ShowData;

public class Show {
  private Long id;
  private LocalDateTime startTime;
  private Movie movie;
  private Theatre theatre;
  private float price;

  public Show(Long id, LocalDateTime startTime, int duration, String movieName,
      String theatreName, float price) {
    this.id = id;
    this.startTime = startTime;
    this.movie = new Movie(movieName, duration);
    this.theatre = new Theatre(theatreName);
    this.price = price;
  }

  public Show(Long id, LocalDateTime startTime, int duration, String movieName,
      String idCoverImage, String theatreName, float price,
      List<SeatData> seats) {
    this.id = id;
    this.startTime = startTime;
    this.movie = new Movie(movieName, duration, idCoverImage);
    this.theatre = new Theatre(theatreName, seats);
    this.price = price;
  }

  public Show(ShowData showdata) {
    this(showdata.idShow(), showdata.startTime(), showdata.movieDuration(),
        showdata.movieName(), showdata.idMovieCoverImage(),
        showdata.theatreName(), showdata.price(), showdata.seats());
  }

  public Show(PlayingData pData) {
    this(pData.idShow(), pData.startTime(), pData.duration(), pData.movieName(),
        pData.theatreName(), pData.price());
  }

  public ShowTimeRecord toTime() {
    return new ShowTimeRecord(this.id, this.startDayTime(), this.finishAtTime(),
        this.theatre.name(), this.price);
  }


  public ShowRecord toRecord() {
    return new ShowRecord(this.id, this.movie.name(), this.movie.duration(),
        this.startDayTime(), this.finishAtTime(), this.theatre.name(),
        theatre.seats(), this.price);
  }

  float price(int numberOfSeatsConfirmed) {
    return this.price * numberOfSeatsConfirmed;
  }

  String movieName() {
    return this.movie.name();
  }

  List<Integer> seatNumbers(List<Long> ids) {
    return this.theatre.numbers(ids);
  }

  String startDayTime() {
    return new DayTimeFormatted(startTime).toString();
  }

  private String finishAtTime() {
    return new TimeFormatted(this.startTime.plusMinutes(movie.duration()))
        .toString();
  }

  String theaterName() {
    return this.theatre.name();
  }
}
