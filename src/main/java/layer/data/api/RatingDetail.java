package layer.data.api;

import java.time.LocalDateTime;

public record RatingDetail(String username, LocalDateTime votedAt, int value,
    String comment) {

}
