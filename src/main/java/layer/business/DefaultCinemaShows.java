package layer.business;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import layer.business.api.CinemaShows;
import layer.business.api.ShowRecord;
import layer.data.api.ShowsDataService;

public class DefaultCinemaShows implements CinemaShows {

  private ShowsDataService showData;

  public DefaultCinemaShows(ShowsDataService showData) {
    this.showData = showData;
  }

  @Override
  public Iterable<ShowRecord> playingThisWeek() {
    var playing = this.showData.playingNow(LocalDateTime.now().plusWeeks(1));
    return playing.stream()
        .map(p -> new Show(p.idShow(), p.startTime(), p.duration(),
            p.movieName(), p.idCoverImage(), p.theatreName()).toRecord())
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public ShowRecord show(Long id) {
    return new Show(this.showData.show(id)).toRecord();
  }
}
