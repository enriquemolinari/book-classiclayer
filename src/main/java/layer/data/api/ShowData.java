package layer.data.api;

import java.time.LocalDateTime;
import java.util.List;

public record ShowData(Long idShow, LocalDateTime startTime, String movieName,
    int movieDuration, String idMovieCoverImage, List<SeatData> seats) {

}
