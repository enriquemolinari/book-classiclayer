package layer.business.api;

public interface Movies {

  Iterable<MovieRecord> movies();

  MovieRecord detail(Long id);

  void rateMovie(Long userId, Long idMovie, int rateValu, String comment);
}
