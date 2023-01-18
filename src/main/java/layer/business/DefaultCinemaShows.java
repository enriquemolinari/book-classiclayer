package layer.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.CinemaException;
import layer.business.api.CinemaShows;
import layer.business.api.CreditCardRecord;
import layer.business.api.ShowRecord;
import layer.business.api.TicketRecord;
import layer.data.api.DataException;
import layer.data.api.ShowsDataService;
import layer.data.api.UserAuthDataService;

public class DefaultCinemaShows implements CinemaShows {

  private ShowsDataService showData;
  private UserAuthDataService userData;
  private static final int POINTS_PER_PURCHASE = 10;
  private EmailService emailService;
  private PaymentProvider payment;

  public DefaultCinemaShows(ShowsDataService showData,
      UserAuthDataService userData, EmailService emailService,
      PaymentProvider payment) {
    this.showData = showData;
    this.userData = userData;
    this.emailService = emailService;
    this.payment = payment;
  }

  @Override
  public Iterable<ShowRecord> playingThisWeek() {
    var playing = this.showData.playingNow(LocalDateTime.now().plusWeeks(1));
    return playing.stream()
        .map(p -> new Show(p.idShow(), p.startTime(), p.duration(),
            p.movieName(), p.idCoverImage(), p.theatreName(), p.price())
                .toRecord())
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public ShowRecord show(Long id) {
    return new Show(this.showData.show(id)).toRecord();
  }

  @Override
  public TicketRecord pay(CreditCardRecord card, Long idShow, Long idUser,
      List<Long> idSeats) {

    if (!this.showData.isReservedBy(idShow, idUser, idSeats)) {
      throw new CinemaException("Seats must be reserved first");
    }

    var showd = this.showData.show(idShow);
    var userd = this.userData.details(idUser);
    var show = new Show(showd);

    var totalAmount = show.price(idSeats.size());
    var points = userd.data().points() + POINTS_PER_PURCHASE;

    this.payment.charge(card, totalAmount);

    try {
      var showConfirmed =
          this.showData.confirm(idShow, idUser, idSeats, totalAmount, points);

      var ticket = new Ticket(showConfirmed, show, show.seatNumbers(idSeats),
          totalAmount, points);

      emailService.send(userd.email(), "From Cinema", ticket.detail());

      return ticket.toRecord();

    } catch (DataException de) {
      throw new CinemaException(de, de.getMessage());
    }
  }

  @Override
  public void makeReservation(Long idShow, Long idUser, List<Long> idSeats) {
    try {
      this.showData.reserve(idShow, idUser, idSeats);
    } catch (DataException de) {
      throw new CinemaException(de,
          "It seems some of the seats you choose has already been reserved.");
    }
  }
}
