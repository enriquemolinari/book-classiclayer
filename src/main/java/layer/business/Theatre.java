package layer.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.SeatRecord;
import layer.data.api.SeatData;

class Theatre {

  private Long id;
  private String name;
  private List<Seat> seats;

  public Theatre(String name) {
    this.name = name;
    this.seats = new ArrayList<>();
  }

  public Theatre(Long id, String name) {
    this(name);
    this.id = id;
  }

  public Theatre(Long id, String name, List<SeatData> seats) {
    this(id, name);
    for (SeatData seatData : seats) {
      this.seats.add(new Seat(seatData.idSeat(), seatData.number(),
          (!seatData.confirmed() || !seatData.reserved())));
    }
  }

  Iterable<SeatRecord> seats() {
    return seats.stream().map(s -> s.toRecord())
        .collect(Collectors.toUnmodifiableList());
  }

  String name() {
    return name;
  }
}
