package layer.data.api;

import java.time.LocalDateTime;

public record PlayingRecord(Long idShow, LocalDateTime startTime,
    String movieName, String idCoverImage) {

}
