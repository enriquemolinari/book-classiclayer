package layer.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.TicketRecord;
import layer.data.api.ShowConfirmed;

public class Ticket {

  private Long id;
  private LocalDateTime payedDate;
  private float total;
  private int totalUserPoints;
  private Show show;
  private List<Integer> seatsBought;

  public Ticket(ShowConfirmed confirmed, Show show, List<Integer> seatsBought,
      float total, int points) {
    this.id = confirmed.saleNumber();
    this.payedDate = confirmed.payDate();
    this.show = show;
    this.seatsBought = List.copyOf(seatsBought);
    this.total = total;
    this.totalUserPoints = points;
  }

  public TicketRecord toRecord() {
    return new TicketRecord(this.seatsBought,
        new DayTimeFormatted(payedDate).toString(), this.show.movieName(),
        this.total, this.totalUserPoints);
  }

  public String detail() {
    var seatsText = "Your seat number is";
    if (seatsBought.size() > 1) {
      seatsText = "Your seat numbers are";
    }
    return String.format(
        "Good news!! Ticket #%s.%nYou will see the \"%s\" movie "
            + "that will be screened on %s this %s.%n%s: %s.%nNow you have %d points.",
        id, this.show.movieName(), this.show.theaterName(),
        this.show.startDayTime(), seatsText, seatsBought.stream()
            .map(i -> String.valueOf(i)).collect(Collectors.joining(",")),
        totalUserPoints);
  }
}
