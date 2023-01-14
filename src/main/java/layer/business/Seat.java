package layer.business;

import layer.business.api.SeatRecord;

public class Seat {

  private Long id;
  private int number;
  private boolean available;

  public Seat(Long id, int number) {
    this.id = id;
    this.number = number;
  }

  public Seat(Long id, int number, boolean available) {
    this(id, number);
    this.available = available;
  }

  SeatRecord toRecord() {
    return new SeatRecord(id, number, available);
  }

}
