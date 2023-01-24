package layer.business.api;

public record ShowRecord(Long id, String movieName, int duration,
    String startDayTime, String finishTime, String theaterName,
    Iterable<SeatRecord> seats, float price) {
}
