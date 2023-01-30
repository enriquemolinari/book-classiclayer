package layer.data.api;

public interface RatingDataService {

  RatingData rate(Long idMovie);

  void rate(Long idUser, Long idMovie, int value, String comment)
      throws DataException;

  void checkUserHasRated(Long idUser, Long idMovie);

}
