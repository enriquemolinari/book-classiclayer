package layer.data.api;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowsDataService {

  List<PlayingData> playingNow(LocalDateTime showsUntil);

  ShowData show(Long idShow);

  boolean isReservedBy(Long idShow, Long idUser, List<Long> idSeats);

  void reserve(Long idShow, Long idUser, List<Long> idSeats)
      throws DataException;

  ShowConfirmed confirm(Long idShow, Long idUser, List<Long> idSeats,
      float totalAmount, int newPoints) throws DataException;
}
