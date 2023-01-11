package layer.data.api;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowsData {

  List<PlayingRecord> playingNow(LocalDateTime showsUntil);

  ShowRecord show(Long idShow);

  void reserve(Long idShow, Long idUser, List<Long> idSeats)
      throws DataException;

  void confirm(Long idShow, Long idUser, List<Long> idSeats)
      throws DataException;
}
