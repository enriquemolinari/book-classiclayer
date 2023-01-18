package layer.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import layer.business.api.SeatRecord;
import layer.data.api.SeatData;

class Theatre {

  private Long id;
  private String name;
  private List<Seat> seats = new ArrayList<>();

  public Theatre(String name, List<SeatData> seats) {
    this(name);

    for (SeatData seatData : seats) {
      this.seats.add(new Seat(seatData.idSeat(), seatData.number(),
          (!seatData.confirmed() || !seatData.reserved())));
    }
  }

  public Theatre(String theatreName) {
    this.name = theatreName;
  }

  public Theatre(Long id, String name, List<SeatData> seats) {
    this(name, seats);
    this.id = id;
  }

  List<Integer> numbers(List<Long> ids) {
    return this.seats.stream().filter(s -> ids.contains(s.id()))
        .map(s -> s.number()).collect(Collectors.toUnmodifiableList());
  }

  Iterable<SeatRecord> seats() {
    return seats.stream().map(s -> s.toRecord())
        .collect(Collectors.toUnmodifiableList());
  }

  String name() {
    return name;
  }
}
