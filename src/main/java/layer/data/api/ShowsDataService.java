package layer.data.api;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowsDataService {

  List<PlayingData> playingNow(LocalDateTime showsUntil);

  ShowData show(Long idShow);

  void reserve(Long idShow, Long idUser, List<Long> idSeats)
      throws DataException;

  void confirm(Long idShow, Long idUser, List<Long> idSeats)
      throws DataException;
}
