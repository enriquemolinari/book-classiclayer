package layer.data.api;

import java.math.BigDecimal;

public interface RatingDataService {

  RatingData rate(Long idMovie);

  void rate(Long idUser, Long idMovie, BigDecimal value) throws DataException;

}
