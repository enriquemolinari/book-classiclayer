package layer.data.api;

public interface RatingData {

  RatingRecord rate(Long idMovie);

  void rate(Long idUser, Long idMovie, int value) throws DataException;

}
