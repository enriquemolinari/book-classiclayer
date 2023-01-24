package layer.data.api;

import java.time.LocalDateTime;
import java.util.List;

public record PlayingData(Long idShow, LocalDateTime startTime, Long movieId,
    String movieName, int duration, String coverImg, List<String> genres,
    String theatreName, float price) {

}
