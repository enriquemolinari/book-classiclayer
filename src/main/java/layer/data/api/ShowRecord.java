package layer.data.api;

import java.time.LocalDateTime;
import java.util.List;

public record ShowRecord(Long idShow, LocalDateTime startTime, String movieName,
    int movieDuration, String idMovieCoverImage, List<SeatRecord> seats) {

}
