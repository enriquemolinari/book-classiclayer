package layer.data.api;

import java.time.LocalDateTime;

public record PlayingData(Long idShow, LocalDateTime startTime,
    String movieName, int duration, String idCoverImage, String theatreName) {

}
