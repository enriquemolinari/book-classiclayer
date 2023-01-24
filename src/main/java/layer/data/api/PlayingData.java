package layer.data.api;

import java.time.LocalDateTime;

public record PlayingData(Long idShow, LocalDateTime startTime, Long movieId,
    String movieName, int duration, String theatreName, float price) {

}
