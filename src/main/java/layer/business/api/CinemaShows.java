package layer.business.api;

public interface CinemaShows {

  Iterable<ShowRecord> playingThisWeek();

  ShowRecord show(Long id);
}
