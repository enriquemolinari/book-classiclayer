package layer.business;

import java.time.LocalDateTime;
import layer.business.api.CinameShows;
import layer.data.api.ShowsDataService;

public class DefaultCinemaShows implements CinameShows {

  private ShowsDataService showData;

  public DefaultCinemaShows(ShowsDataService showData) {
    this.showData = showData;
  }

  @Override
  public void playingThisWeek() {
    var playing = this.showData.playingNow(LocalDateTime.now().plusWeeks(1));
    // playing
  }

}
