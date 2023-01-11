package layer.data.api;

public record SeatRecord(Long idSeat, int number, boolean reserved,
    boolean confirmed) {
}
