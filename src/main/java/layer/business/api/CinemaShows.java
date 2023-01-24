package layer.business.api;

import java.util.List;

public interface CinemaShows {

  Iterable<MovieShows> playingThisWeek();

  ShowRecord show(Long id);

  TicketRecord pay(CreditCardRecord card, Long idShow, Long idUser,
      List<Long> idSeats);

  void makeReservation(Long idShow, Long idUser, List<Long> idSeats);

}
