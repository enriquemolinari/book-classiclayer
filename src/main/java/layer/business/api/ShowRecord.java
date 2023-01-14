package layer.business.api;

public record ShowRecord(Long id, String movieName, String startDayTime,
    String finishTime, String movieCover, String theaterName,
    Iterable<SeatRecord> seats) {

}
